package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.control.DriveControl;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class RobotContainer {
  private final DriveSubsystem drive = new DriveSubsystem();
  private final VisionSubsystem vision = new VisionSubsystem();
  private final XboxController driver = new XboxController(Constants.Operator.DRIVER_USB_PORT);

  public RobotContainer() {
    drive.setDefaultCommand(drive.runEnd(this::driveWithController, () -> {
      drive.stop();
      SmartDashboard.putBoolean("Vision/AimActive", false);
    }).withName("Teleop drive with optional AprilTag aiming"));
    SmartDashboard.putString("Autonomous/Mode", "Do nothing (hold drivetrain stopped)");
    SmartDashboard.putBoolean("Vision/AimActive", false);
  }

  private void driveWithController() {
    var output = DriveControl.calculate(
        DriverStation.isTeleopEnabled(),
        -driver.getLeftY(),
        -driver.getRightX(),
        driver.getLeftBumperButton(),
        driver.getRightBumperButton(),
        vision.getTarget());
    drive.arcadeDrive(output.forward(), output.turn());
    SmartDashboard.putBoolean("Vision/AimActive", output.aiming());
  }

  public Command getAutonomousCommand() {
    // A continuous command owns the drivetrain throughout autonomous.
    return drive.run(drive::stop).withName("Do nothing autonomous");
  }
}
