package frc.robot.vision;

import static org.junit.jupiter.api.Assertions.*;

import frc.robot.Constants;
import frc.robot.control.DriveControl;
import frc.robot.vision.VisionTracker.Frame;
import frc.robot.vision.VisionTracker.Target;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VisionTrackerTest {
  @Test
  void cachesTargetBetweenCameraFramesButExpiresAfterQuarterSecond() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0, target(1, 12.0))), 10.0);
    tracker.update(true, List.of(), 10.1);

    assertEquals(1, tracker.target(true, 10.1).orElseThrow().id());
    assertTrue(tracker.target(true, 10.0 + Constants.Vision.MAX_FRAME_AGE_SECONDS).isPresent());
    assertTrue(tracker.target(true, 10.251).isEmpty());
  }

  @Test
  void newerEmptyFrameClearsTargetAndOlderFramesCannotResurrectIt() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0, target(1, 10.0))), 10.0);
    tracker.update(true, List.of(frame(10.1)), 10.1);
    assertTrue(tracker.target(true, 10.1).isEmpty());

    tracker.update(true, List.of(frame(10.05, target(2, 5.0))), 10.15);
    assertTrue(tracker.target(true, 10.15).isEmpty());
    assertEquals(0.05, tracker.frameAgeSeconds(10.15), 1e-9);
  }

  @Test
  void newestCaptureWinsEvenWhenUnreadFramesArriveOutOfOrder() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(
        frame(10.1, target(2, 8.0)),
        frame(10.0, target(1, 2.0)),
        frame(10.05, target(3, 3.0))), 10.1);

    assertEquals(2, tracker.target(true, 10.1).orElseThrow().id());
  }

  @Test
  void disconnectClearsCachedTargetUntilANewFrameArrives() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0, target(1, 5.0))), 10.0);
    assertTrue(tracker.target(false, 10.0).isEmpty());
    tracker.update(false, List.of(), 10.01);
    tracker.update(true, List.of(), 10.02);
    assertTrue(tracker.target(true, 10.02).isEmpty());

    tracker.update(true, List.of(frame(10.03, target(2, -5.0))), 10.03);
    assertEquals(2, tracker.target(true, 10.03).orElseThrow().id());
  }

  @Test
  void badTimestampCannotPoisonCacheAndHideNextValidFrame() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(
        frame(Double.NaN, target(1, 1.0)),
        frame(Double.POSITIVE_INFINITY, target(2, 1.0)),
        frame(-1.0, target(3, 1.0)),
        frame(1000.0, target(4, 1.0)),
        frame(10.0, target(5, 10.0))), 10.0);

    assertEquals(5, tracker.target(true, 10.0).orElseThrow().id());
    tracker.update(true, List.of(frame(10.1, target(6, 8.0))), 10.1);
    assertEquals(6, tracker.target(true, 10.1).orElseThrow().id());
  }

  @Test
  void allowsSmallClockSkewButRejectsLargeFutureOffset() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.01, target(1, 5.0))), 10.0);
    assertTrue(tracker.target(true, 10.0).isPresent());
    assertTrue(tracker.target(true, 9.9).isEmpty());

    var futureOnly = new VisionTracker();
    futureOnly.update(true, List.of(frame(10.021, target(1, 5.0))), 10.0);
    assertTrue(futureOnly.target(true, 10.0).isEmpty());
  }

  @Test
  void nonfiniteRobotClockDoesNotYieldAUsableTarget() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0, target(1, 5.0))), 10.0);
    assertTrue(tracker.target(true, Double.NaN).isEmpty());
    assertTrue(tracker.target(true, Double.POSITIVE_INFINITY).isEmpty());
    tracker.update(true, List.of(), Double.NaN);
    assertTrue(tracker.target(true, 10.0).isEmpty());
  }

  @Test
  void selectsNearestValidTagAndRejectsBadGeometryOrAmbiguity() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0,
        new Target(-1, 0.0, 0.0),
        new Target(1, Double.NaN, 0.0),
        new Target(2, Double.POSITIVE_INFINITY, 0.0),
        new Target(3, 181.0, 0.0),
        new Target(4, 0.0, -1.0),
        new Target(5, 0.0, Double.NaN),
        new Target(6, 0.0, Double.POSITIVE_INFINITY),
        new Target(7, 0.0, 0.21),
        target(8, 15.0),
        new Target(9, -4.0, Constants.Vision.MAX_POSE_AMBIGUITY))), 10.0);

    assertEquals(9, tracker.target(true, 10.0).orElseThrow().id());
  }

  @Test
  void newerUnusableFrameDoesNotKeepPreviousGoodTarget() {
    var tracker = new VisionTracker();
    tracker.update(true, List.of(frame(10.0, target(1, 4.0))), 10.0);
    tracker.update(true, List.of(frame(10.1, new Target(1, 3.0, 0.8))), 10.1);
    assertTrue(tracker.target(true, 10.1).isEmpty());
  }

  @Test
  void targetLossAndStalenessRestoreDriverSteeringWhileAimIsHeld() {
    var tracker = new VisionTracker();
    var manual = DriveControl.calculate(true, 0.4, 0.5, false, true, Optional.empty());
    tracker.update(true, List.of(frame(10.0, target(1, 20.0))), 10.0);
    var aiming = DriveControl.calculate(true, 0.4, 0.5, false, true, tracker.target(true, 10.1));
    assertTrue(aiming.aiming());
    assertNotEquals(manual.turn(), aiming.turn());

    var expired = DriveControl.calculate(true, 0.4, 0.5, false, true, tracker.target(true, 10.3));
    assertEquals(manual, expired);
    tracker.update(true, List.of(frame(10.31)), 10.31);
    var lost = DriveControl.calculate(true, 0.4, 0.5, false, true, tracker.target(true, 10.31));
    assertEquals(manual, lost);
  }

  private static Frame frame(double timestamp, Target... targets) {
    return new Frame(timestamp, List.of(targets));
  }

  private static Target target(int id, double yaw) {
    return new Target(id, yaw, 0.05);
  }
}
