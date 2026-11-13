# Contributing

Maine Community Robotics welcomes fixes, examples, and documentation that help teams understand and extend a base FRC robot.

Open an issue describing the problem or proposed improvement, then submit a focused pull request. Explain the resulting robot behavior, the hardware assumptions, and how you verified the change. Small, readable examples are especially useful for students and mentors.

## Development

Use Java 17, WPILib 2026, and the checked-in Gradle wrapper. Run:

```sh
./gradlew build
```

Use `./gradlew test` for a focused test run and `./gradlew simulateJava` for the WPILib simulation GUI. On Windows, use `gradlew.bat` or `.\gradlew.bat`. Simulation has no drivetrain or camera physics model; describe actual hardware checks separately.

Add behavior tests when changing control decisions, target acceptance, timestamps, or command lifecycle. Useful cases include target loss, stale frames, left/right yaw signs, command interruption, and autonomous behavior. Avoid tests that only repeat constants or implementation details.

## Keep the template reusable

- Leave the committed team number at `0` and `HARDWARE_CONFIGURED` at `false`.
- Keep wiring, inversion, and tuning values explicit, with units where relevant.
- Preserve manual steering fallback, bounded vision outputs, MotorSafety, and teleop-only driver control.
- Do not add automatic forward motion or a moving autonomous routine without clear configuration and documentation.
- Pin dependencies and review WPILib, PhotonLib, and coprocessor compatibility together. Update the documented versions and verify rotation conventions when upgrading.
- Keep credentials, robot logs, build output, and local IDE state out of commits.

Document limitations directly. A passing software build does not establish correct wiring, motor direction, camera mounting, or gain tuning on a physical robot.

Project contributions use the repository's [MIT license](LICENSE). Preserve third-party notices and licenses when including external material.
