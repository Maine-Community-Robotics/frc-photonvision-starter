# PhotonVision setup

PhotonVision runs on a separate vision coprocessor connected to the robot network. PhotonLib in this robot project reads its detections through NetworkTables. This template uses AprilTag yaw to help the driver aim; it does not estimate field position or drive toward a target.

## Configure the coprocessor and camera

1. Install **PhotonVision v2026.3.4**, matching [`vendordeps/photonlib.json`](../vendordeps/photonlib.json). Use the [official release](https://github.com/PhotonVision/photonvision/releases/tag/v2026.3.4) and [installation guide](https://docs.photonvision.org/en/latest/docs/quick-start/quick-install.html) for your hardware.
2. Connect the coprocessor and camera to the robot network. Open its web interface at `http://COPROCESSOR_ADDRESS:5800`, replacing the address with the device's actual hostname or IP.
3. Configure PhotonVision for your FRC team and use the roboRIO's NetworkTables server. Do not run a separate NetworkTables server on the coprocessor for this robot configuration.
4. Set the camera nickname to **`mcr-front`**, or change the camera name in [`Constants.java`](../src/main/java/frc/robot/Constants.java) to match exactly. Give each camera a unique name.
5. Mount the camera upright and facing forward, with its horizontal aiming axis aligned with the robot. Configure image rotation appropriately. Calibrate the camera for the resolution you will use.
6. Create an AprilTag pipeline and match the pipeline index configured in the robot constants. Select the tag family appropriate for the tags you are using. Enable 3D solving so the AprilTag pose-ambiguity value is available; this template rejects the unavailable value `-1`.
7. Verify tag detection and the camera's yaw values in the PhotonVision web interface before enabling steering assistance.

Calibration and 3D solving provide the quality measurement used by the filter. The robot still uses only horizontal yaw for steering. A camera mounted at an angle needs a deliberate coordinate conversion; the default code assumes a forward-aligned camera.

## Aiming behavior

Hold the Xbox controller's **right bumper** during teleop to use a valid AprilTag for steering. The driver continues controlling forward/backward movement with the left stick. Release the bumper to resume manual steering.

Among acceptable tags in the newest frame, the code selects the tag with the smallest absolute yaw, meaning the tag nearest the camera's horizontal center. This is a demonstration policy, not a season-specific scoring-target selector; the selected tag can change as the camera view changes.

Default acceptance and control values:

| Setting | Value |
| --- | --- |
| Fiducial ID | At least 0 |
| Pose ambiguity | Between 0 and 0.20, inclusive |
| Maximum capture age | 0.250 seconds |
| Maximum future timestamp tolerance | 0.020 seconds |
| Aim tolerance | 2 degrees |
| Proportional turn gain | 0.015 per degree |
| Maximum vision turn request | 0.30 |

Robot drive-output limits and precision mode also limit motion. These starter tuning values require checking on your own robot.

The vision subsystem drains the unread-result queue once per robot loop and uses the newest frame. A usable observation can remain active between camera frames until it expires. A new accepted frame with no acceptable tag, a disconnected camera, or an expired observation restores manual steering immediately. Frames with invalid timestamps are ignored; they cannot extend the previous observation's lifetime. Target age uses the capture timestamp synchronized to the roboRIO FPGA clock, rather than the time the code happened to receive a frame.

## Rotation conventions

For the pinned PhotonVision **v2026.3.4** yaw calculation, positive target yaw means the target is to the **right**. WPILib **2026.2.1** uses positive `DifferentialDrive` rotation for a **counterclockwise** turn. The robot therefore uses `-kP * yaw`: a target on the right requests a negative, clockwise turn.

Check signs against the pinned [PhotonVision yaw calculation](https://github.com/PhotonVision/photonvision/blob/v2026.3.4/photon-core/src/main/java/org/photonvision/vision/target/TargetCalculations.java) and [DifferentialDrive source](https://github.com/wpilibsuite/allwpilib/blob/v2026.2.1/wpilibj/src/main/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.java) when upgrading. Some PhotonVision documentation describes yaw differently; verify the actual detection and wheel directions before using steering assistance. Normal Xbox forward and right-turn axes are negated to match WPILib's forward-positive, counterclockwise-positive convention.

## Verify on the robot

First complete the wheel-direction checks in [Getting started](GETTING_STARTED.md). Keep the drivetrain raised for the initial vision check, and leave the forward stick centered.

1. Present a calibrated, recognized AprilTag on the camera's left. Check that `Vision/TargetYawDegrees` is negative.
2. Hold the right bumper. The wheel outputs should correspond to a left turn. Repeat with the tag on the right, then centered.
3. Cover the camera or remove the tag. Verify vision assistance stops and the right stick controls steering again. Repeat after disconnecting the camera/coprocessor connection.
4. Release the bumper and verify manual steering resumes. Disable the robot and verify the motors stop.
5. After those checks, test at low speed in a clear area and adjust the gain, tolerance, and output limits for your drivetrain.

## Dashboard and troubleshooting

| Dashboard value | Meaning |
| --- | --- |
| `Drive/HardwareConfigured` | Whether the motor-output configuration flag is enabled |
| `Vision/Connected` | Camera connection status |
| `Vision/HasUsableTarget` | Whether a target currently passes the acceptance checks |
| `Vision/TargetYawDegrees` | Selected target's horizontal yaw |
| `Vision/TargetId` | Selected AprilTag ID |
| `Vision/FrameAgeSeconds` | Age of the newest frame |
| `Vision/AimActive` | Whether vision steering assistance is active |

If tags appear in the camera view but are unusable, check ambiguity, 3D solving, calibration, frame age, and camera time synchronization. A connection alone does not guarantee fresh target data. Check the target-validity indicator before interpreting yaw or ID telemetry.

If the camera is disconnected, check the exact nickname, team/network configuration, and coprocessor power. For version or time-synchronization alerts, use matching PhotonVision/PhotonLib versions and correct the connection problem rather than suppressing alerts.

References: [PhotonVision quick start](https://docs.photonvision.org/en/latest/docs/quick-start/quick-install.html), [camera calibration](https://docs.photonvision.org/en/latest/docs/calibration/calibration.html), and [PhotonLib target data](https://docs.photonvision.org/en/latest/docs/programming/photonlib/getting-target-data.html). Latest documentation may describe newer versions; pinned source links above describe this template's rotation conventions.
