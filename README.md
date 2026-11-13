# Maine Community Robotics — FRC PhotonVision Starter

[![Build](https://github.com/Maine-Community-Robotics/frc-photonvision-starter/actions/workflows/build.yml/badge.svg)](https://github.com/Maine-Community-Robotics/frc-photonvision-starter/actions/workflows/build.yml)

A small, command-based Java starting point for Maine teams learning FIRST Robotics Competition programming. It includes a differential drivetrain, Xbox controls, and PhotonVision AprilTag aiming.

[Repository](https://github.com/Maine-Community-Robotics/frc-photonvision-starter) · [Use this template](https://github.com/Maine-Community-Robotics/frc-photonvision-starter/generate)

**Motor output starts disabled.** Configure your hardware and set `Constants.Drive.HARDWARE_CONFIGURED` to `true` before driving a robot. The team number is also a placeholder (`0`).

## Included

- Four PWM motor controllers, with two motors per side.
- Arcade drive, precision mode, and hold-to-aim AprilTag steering.
- Camera connection, target quality, and frame-age checks with manual steering fallback.
- Dashboard telemetry, a stationary autonomous command, and automated build/test configuration.
- A Gradle wrapper and pinned vendor dependencies.

Vision controls steering only while the aim button is held. The driver controls forward movement throughout. This template does not include range control, odometry, field pose estimation, swerve, or a moving autonomous routine.

## Quick start

1. [Use this template](https://github.com/Maine-Community-Robotics/frc-photonvision-starter/generate) to create your own repository, then clone it. If you received a ZIP, extract it first.
2. Install [WPILib 2026](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html), including its Java development tools, and open the project folder in WPILib VS Code.
3. Set your team number in [`.wpilib/wpilib_preferences.json`](.wpilib/wpilib_preferences.json). Review [hardware constants](src/main/java/frc/robot/Constants.java) before enabling motor output.
4. Build the project:

   ```sh
   ./gradlew build
   ```

   On Windows, use `gradlew.bat build` in Command Prompt or `.\gradlew.bat build` in PowerShell.
5. Follow [Getting started](docs/GETTING_STARTED.md) for wiring, controls, simulation, and first deployment, then [PhotonVision setup](docs/PHOTONVISION.md) to enable aiming.

## Driver controls

Xbox controller on USB port **0**:

| Control | Action |
| --- | --- |
| Left stick Y | Drive forward/backward |
| Right stick X | Turn left/right |
| Hold left bumper | Precision mode |
| Hold right bumper | Aim at a usable AprilTag; retain manual forward control |
| Release right bumper | Return to manual steering |

If the camera disconnects, the target disappears, or its data becomes stale or fails quality checks, manual steering resumes immediately. Autonomous holds the drivetrain stopped.

## Toolchain

| Component | Pinned version |
| --- | --- |
| Java | 17 |
| WPILib / GradleRIO | [2026.2.1](https://github.com/wpilibsuite/allwpilib/releases/tag/v2026.2.1) |
| Gradle wrapper | 8.14.3 |
| PhotonLib / PhotonVision | [v2026.3.4](https://github.com/PhotonVision/photonvision/releases/tag/v2026.3.4) |

Install the matching PhotonVision version on the coprocessor. Review WPILib, vendor dependencies, and coprocessor software together when upgrading seasons.

## Use this template for your robot

Maintainers: enable **Settings → General → Template repository** once to make the template button and commands below available.

Select [Use this template → Create a new repository](https://github.com/Maine-Community-Robotics/frc-photonvision-starter/generate), choose the owning account, repository name, and visibility, then clone your new repository.

Alternatively, use the [GitHub CLI](https://cli.github.com/). Replace `YOUR_ACCOUNT` and `YOUR_ROBOT_REPO` with your own names. This creates a private repository; use `--public` if you want it publicly available.

```sh
gh auth login
gh repo create YOUR_ACCOUNT/YOUR_ROBOT_REPO --private --template Maine-Community-Robotics/frc-photonvision-starter --clone
```

Open your new repository and follow the quick start above to configure your team, hardware, and camera.

Contributions are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md). Project code is available under the [MIT license](LICENSE); dependencies retain their own licenses.
