package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/** Example PWM differential drivetrain. Replace the motor layer for your actual hardware. */
public class DriveSubsystem extends SubsystemBase {
  private final PWMSparkMax leftFront = new PWMSparkMax(Constants.Drive.LEFT_FRONT_PWM);
  private final PWMSparkMax leftRear = new PWMSparkMax(Constants.Drive.LEFT_REAR_PWM);
  private final PWMSparkMax rightFront = new PWMSparkMax(Constants.Drive.RIGHT_FRONT_PWM);
  private final PWMSparkMax rightRear = new PWMSparkMax(Constants.Drive.RIGHT_REAR_PWM);
  private final DifferentialDrive drive = new DifferentialDrive(
      output -> { leftFront.set(output); leftRear.set(output); },
      output -> { rightFront.set(output); rightRear.set(output); });

  public DriveSubsystem() {
    leftFront.setInverted(Constants.Drive.LEFT_INVERTED);
    leftRear.setInverted(Constants.Drive.LEFT_INVERTED);
    rightFront.setInverted(Constants.Drive.RIGHT_INVERTED);
    rightRear.setInverted(Constants.Drive.RIGHT_INVERTED);
    drive.setMaxOutput(Constants.Drive.MAX_OUTPUT);
    drive.setDeadband(0.0); // The control calculation applies joystick deadband once.
    drive.setSafetyEnabled(true);
    SmartDashboard.putBoolean("Drive/HardwareConfigured", Constants.Drive.HARDWARE_CONFIGURED);
  }

  public void arcadeDrive(double forward, double turn) {
    if (!Constants.Drive.HARDWARE_CONFIGURED) {
      stop();
      return;
    }
    drive.arcadeDrive(forward, turn, false);
  }

  public void stop() {
    drive.stopMotor();
  }
}
