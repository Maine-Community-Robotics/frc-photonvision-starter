# Getting started

## 1. Open and build the project

Use the WPILib 2026 installer and WPILib VS Code. This project targets Java 17 and uses the included Gradle wrapper, so a separate Gradle installation is unnecessary. The first build needs internet access to download WPILib, PhotonLib, and test dependencies.

From the repository root:

```sh
./gradlew build
./gradlew test
```

`build` already runs the tests; the second command is useful when you only want to run tests. On Windows, substitute `gradlew.bat` in Command Prompt or `.\gradlew.bat` in PowerShell for `./gradlew`.

Set `teamNumber` in [`.wpilib/wpilib_preferences.json`](../.wpilib/wpilib_preferences.json) to your FRC team number. The checked-in value `0` is a placeholder, not a deployable team configuration.

## 2. Match the code to the robot

Review [`Constants.java`](../src/main/java/frc/robot/Constants.java). The starter assumes:

| Item | Default |
| --- | --- |
| Left motor PWM ports | 0 and 1 |
| Right motor PWM ports | 2 and 3 |
| Motor controller class | WPILib `PWMSparkMax` |
| Left side inversion | Off |
| Right side inversion | On |
| Xbox controller USB port | 0 |
| Maximum drive output | 0.60 |
| Precision-mode multiplier | 0.35 |
| Motor configuration acknowledgment | `HARDWARE_CONFIGURED = false` |

These are **PWM** ports, not CAN IDs. `PWMSparkMax` controls a Spark MAX through its PWM input; a CAN-connected Spark MAX needs REVLib and a different drivetrain implementation. Motors, controllers, and wiring must match the actual robot.

The hardware flag keeps motor commands at zero until you deliberately set `Constants.Drive.HARDWARE_CONFIGURED` to `true`. The dashboard reports its value as `Drive/HardwareConfigured`. Update the wiring constants and inversions first.

The default code has no wheel encoders or gyro. Add those sensors before implementing measured distance driving, odometry, or path following.

## 3. Check the controls in simulation

```sh
./gradlew simulateJava
```

Use the WPILib simulation GUI to assign an Xbox controller to joystick port 0 and select teleoperated mode. Enable only when ready to inspect the controls. Joystick input does not drive the robot in disabled or autonomous mode.

This starter provides the WPILib simulation launcher, but no drivetrain physics or simulated camera scene. The GUI can show mode, input, and PWM behavior; it does not demonstrate realistic robot movement or camera detections. With the hardware flag still false, commanded motor outputs remain zero. Camera-disconnected status is expected without a connected PhotonVision instance.

## 4. First deployment and wheel-direction check

1. With robot power off, verify motor controller models, PWM connections, drivetrain wiring, and configured channels with a mentor.
2. Secure the robot with its drive wheels clear of the floor. Keep people and loose items clear of moving parts.
3. Set `Constants.Drive.HARDWARE_CONFIGURED` to `true` after reviewing the hardware configuration, then rebuild.
4. Connect to the robot network. Use **WPILib: Deploy Robot Code** in VS Code or run `./gradlew deploy`. Deployment installs robot software; it does not enable the robot.
5. In Driver Station, confirm the correct team, communication, and Xbox controller on USB port 0. Start disabled. Check `Drive/HardwareConfigured` on the dashboard.
6. Enable teleop and use small stick inputs while holding the left bumper. Pushing the left stick forward should command both sides forward; moving the right stick right should turn the robot right. Disable immediately if directions differ, then correct the inversion constants and redeploy.
7. Release the controls and verify the wheels stop. Disable and verify they remain stopped. Autonomous should also hold the drivetrain at zero.

The normal maximum drive output is 60%, with a 0.35 precision multiplier. These are starter settings; tune them for your robot after confirming directions. MotorSafety remains enabled, so drivetrain commands must continue updating periodically.

## 5. Add vision

Follow [PhotonVision setup](PHOTONVISION.md). Without a usable target, the robot remains manually steerable. The right bumper enables steering assistance only; it never commands automatic forward movement.

## Where to make changes

| Location | Purpose |
| --- | --- |
| `src/main/java/frc/robot/Constants.java` | Hardware mapping and tuning values |
| `src/main/java/frc/robot/Robot.java` | Robot mode transitions and command scheduler |
| `src/main/java/frc/robot/RobotContainer.java` | Controls, command wiring, and autonomous selection |
| `src/main/java/frc/robot/subsystems/` | Drivetrain and vision behavior |
| `src/test/java/` | Automated behavior checks |
| `vendordeps/` | Pinned vendor-library definitions |
| `.github/workflows/` | GitHub build/test automation |

When adding autonomous behavior, require the drivetrain for the full routine and stop it when the command ends. Preserve the teleop guards on manual controls so a default command cannot introduce joystick motion during autonomous.

References: [WPILib installation](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html), [command-based programming](https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html), and [robot simulation](https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/introduction.html).
