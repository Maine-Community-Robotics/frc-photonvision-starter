package frc.robot.vision;

import frc.robot.Constants;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Tracks the newest camera frame without depending on NetworkTables or robot hardware. */
public final class VisionTracker {
  public record Target(int id, double yawDegrees, double ambiguity) {}
  public record Frame(double timestampSeconds, List<Target> targets) {
    public Frame {
      targets = List.copyOf(targets);
    }
  }

  private Frame latest;

  public void update(boolean connected, List<Frame> unreadFrames, double nowSeconds) {
    if (!connected || !Double.isFinite(nowSeconds)) {
      latest = null;
      return;
    }
    for (Frame frame : unreadFrames) {
      double timestamp = frame.timestampSeconds();
      // A future timestamp must never poison the cache and hide subsequent valid frames.
      if (!Double.isFinite(timestamp)
          || timestamp < 0.0
          || timestamp > nowSeconds + Constants.Vision.MAX_FUTURE_SECONDS) {
        continue;
      }
      if (latest == null || timestamp > latest.timestampSeconds()) {
        // Even an empty or unusable newer frame replaces the previous target immediately.
        latest = frame;
      }
    }
  }

  public Optional<Target> target(boolean connected, double nowSeconds) {
    if (!connected || latest == null || !Double.isFinite(nowSeconds)) {
      return Optional.empty();
    }
    double age = frameAgeSeconds(nowSeconds);
    if (age < -Constants.Vision.MAX_FUTURE_SECONDS
        || age > Constants.Vision.MAX_FRAME_AGE_SECONDS) {
      return Optional.empty();
    }
    return latest.targets().stream()
        .filter(target -> target.id() >= 0)
        .filter(target -> Double.isFinite(target.yawDegrees()) && Math.abs(target.yawDegrees()) <= 180)
        .filter(target -> Double.isFinite(target.ambiguity())
            && target.ambiguity() >= 0.0
            && target.ambiguity() <= Constants.Vision.MAX_POSE_AMBIGUITY)
        .min(Comparator.comparingDouble(target -> Math.abs(target.yawDegrees())));
  }

  public double frameAgeSeconds(double nowSeconds) {
    return latest == null ? -1.0 : nowSeconds - latest.timestampSeconds();
  }
}
