package frc.robot.control;

import static org.junit.jupiter.api.Assertions.*;

import frc.robot.Constants;
import frc.robot.vision.VisionTracker.Target;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DriveControlTest {
  private static final double EPSILON = 1e-9;

  @Test
  void photonRightTargetCommandsClockwiseAndLeftTargetCommandsCounterclockwise() {
    // PhotonVision 2026.3.4 image yaw is positive right; WPILib turn is positive left.
    var right = drive(0.0, 0.0, false, true, target(10.0));
    var left = drive(0.0, 0.0, false, true, target(-10.0));

    assertTrue(right.turn() < 0.0, "A right-hand tag must turn the robot right");
    assertTrue(left.turn() > 0.0, "A left-hand tag must turn the robot left");
    assertEquals(-right.turn(), left.turn(), EPSILON);
    assertTrue(right.aiming());
  }

  @Test
  void largeTargetErrorIsLimitedAndCenteredTargetDoesNotTurn() {
    assertEquals(-Constants.Vision.MAX_TURN,
        drive(0.0, 0.0, false, true, target(90.0)).turn(), EPSILON);
    assertEquals(Constants.Vision.MAX_TURN,
        drive(0.0, 0.0, false, true, target(-90.0)).turn(), EPSILON);
    assertEquals(0.0,
        drive(0.0, 0.8, false, true, target(Constants.Vision.YAW_TOLERANCE_DEGREES)).turn());
  }

  @Test
  void aimingNeverGeneratesForwardMovementAndPreservesDriverThrottle() {
    var stationary = drive(0.0, 0.0, false, true, target(15.0));
    var manual = drive(0.5, 0.0, false, false, Optional.empty());
    var aiming = drive(0.5, 0.0, false, true, target(15.0));

    assertEquals(0.0, stationary.forward());
    assertEquals(manual.forward(), aiming.forward(), EPSILON);
  }

  @Test
  void releasingAimOrLosingTargetRestoresManualSteering() {
    var manual = drive(0.5, 0.6, false, false, Optional.empty());
    assertEquals(manual, drive(0.5, 0.6, false, false, target(10.0)));
    assertEquals(manual, drive(0.5, 0.6, false, true, Optional.empty()));
    assertEquals(manual, drive(0.5, 0.6, false, true, target(Double.NaN)));
  }

  @Test
  void precisionModeScalesThrottleAndAimTogether() {
    var normal = drive(0.6, 0.0, false, true, target(10.0));
    var precision = drive(0.6, 0.0, true, true, target(10.0));
    assertEquals(normal.forward() * Constants.Operator.PRECISION_SCALE,
        precision.forward(), EPSILON);
    assertEquals(normal.turn() * Constants.Operator.PRECISION_SCALE,
        precision.turn(), EPSILON);
    assertTrue(precision.aiming());
  }

  @Test
  void autonomousDisabledAndTestModesCannotDriveFromJoystickOrVision() {
    // Caller supplies DriverStation.isTeleopEnabled(): false in all three modes.
    for (boolean aimHeld : new boolean[] {false, true}) {
      assertEquals(new DriveControl.Output(0.0, 0.0, false),
          DriveControl.calculate(false, 1.0, -1.0, false, aimHeld, target(30.0)));
    }
  }

  @Test
  void joystickDeadbandAndMalformedInputsDoNotCauseMovement() {
    assertEquals(new DriveControl.Output(0.0, 0.0, false),
        drive(0.04, -0.04, false, false, Optional.empty()));
    assertEquals(new DriveControl.Output(0.0, 0.0, false),
        drive(Double.NaN, Double.POSITIVE_INFINITY, false, false, Optional.empty()));
    assertEquals(new DriveControl.Output(1.0, -1.0, false),
        drive(3.0, -3.0, false, false, Optional.empty()));
  }

  private static DriveControl.Output drive(
      double forward, double turn, boolean precision, boolean aim, Optional<Target> target) {
    return DriveControl.calculate(true, forward, turn, precision, aim, target);
  }

  private static Optional<Target> target(double yaw) {
    return Optional.of(new Target(1, yaw, 0.05));
  }
}
