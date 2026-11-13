package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.vision.VisionTracker;
import frc.robot.vision.VisionTracker.Frame;
import frc.robot.vision.VisionTracker.Target;
import java.util.Optional;
import org.photonvision.PhotonCamera;

/** Reads PhotonVision once per scheduler loop and publishes filtered AprilTag observations. */
public class VisionSubsystem extends SubsystemBase {
  private final PhotonCamera camera = new PhotonCamera(Constants.Vision.CAMERA_NAME);
  private final VisionTracker tracker = new VisionTracker();
  private boolean wasConnected;

  public VisionSubsystem() {
    camera.setDriverMode(false);
    camera.setPipelineIndex(Constants.Vision.APRILTAG_PIPELINE_INDEX);
  }

  @Override
  public void periodic() {
    boolean connected = camera.isConnected();
    if (connected && !wasConnected) {
      camera.setDriverMode(false);
      camera.setPipelineIndex(Constants.Vision.APRILTAG_PIPELINE_INDEX);
    }
    wasConnected = connected;
    var frames = camera.getAllUnreadResults().stream()
        .map(result -> new Frame(result.getTimestampSeconds(), result.getTargets().stream()
            .map(target -> new Target(target.getFiducialId(), target.getYaw(), target.getPoseAmbiguity()))
            .toList()))
        .toList();
    double now = Timer.getFPGATimestamp();
    tracker.update(connected, frames, now);
    Optional<Target> target = tracker.target(connected, now);
    SmartDashboard.putBoolean("Vision/Connected", connected);
    SmartDashboard.putBoolean("Vision/HasUsableTarget", target.isPresent());
    SmartDashboard.putNumber("Vision/TargetYawDegrees", target.map(Target::yawDegrees).orElse(0.0));
    SmartDashboard.putNumber("Vision/TargetId", target.map(value -> (double) value.id()).orElse(-1.0));
    SmartDashboard.putNumber("Vision/FrameAgeSeconds", tracker.frameAgeSeconds(now));
  }

  public Optional<Target> getTarget() {
    return tracker.target(camera.isConnected(), Timer.getFPGATimestamp());
  }
}
