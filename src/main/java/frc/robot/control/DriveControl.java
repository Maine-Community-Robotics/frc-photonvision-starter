package frc.robot.control;

import frc.robot.Constants;
import frc.robot.vision.VisionTracker.Target;
import java.util.Optional;

/** Pure control calculation, shared by the robot and desktop tests. Positive turn is left. */
public final class DriveControl {
  public record Output(double forward, double turn, boolean aiming) {}

  private DriveControl() {}

  public static Output calculate(
      boolean teleopEnabled,
      double forwardAxis,
      double turnAxis,
      boolean precision,
      boolean aimHeld,
      Optional<Target> target) {
    if (!teleopEnabled) {
      return new Output(0.0, 0.0, false);
    }
    double forward = deadband(forwardAxis);
    double turn = deadband(turnAxis);
    boolean aiming = aimHeld && target.isPresent() && Double.isFinite(target.get().yawDegrees());
    if (aiming) {
      double yaw = target.get().yawDegrees();
      // PhotonVision v2026.3.4 yaw is positive RIGHT; WPILib rotation is positive LEFT.
      turn = Math.abs(yaw) <= Constants.Vision.YAW_TOLERANCE_DEGREES
          ? 0.0
          : clamp(-yaw * Constants.Vision.TURN_KP_PER_DEGREE, Constants.Vision.MAX_TURN);
    }
    double scale = precision ? Constants.Operator.PRECISION_SCALE : 1.0;
    return new Output(forward * scale, turn * scale, aiming);
  }

  private static double deadband(double value) {
    if (!Double.isFinite(value)) {
      return 0.0;
    }
    value = clamp(value, 1.0);
    double magnitude = Math.abs(value);
    double band = Constants.Operator.DEADBAND;
    return magnitude <= band ? 0.0 : Math.copySign((magnitude - band) / (1.0 - band), value);
  }

  private static double clamp(double value, double limit) {
    return Math.max(-limit, Math.min(limit, value));
  }
}
