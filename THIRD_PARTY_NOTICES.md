# Third-party notices

The original Maine Community Robotics robot code and documentation use the MIT license in `LICENSE`.

- Gradle build infrastructure is adapted from the WPILib 2026 Java template. Its BSD license is retained in [licenses/WPILib-BSD.txt](licenses/WPILib-BSD.txt).
- The Gradle 8.14.3 wrapper scripts and binary are distributed under Apache License 2.0, retained in [licenses/Gradle-Apache-2.0.txt](licenses/Gradle-Apache-2.0.txt). The scripts retain their upstream copyright notices.
- WPILib, PhotonLib, JUnit, and their transitive dependencies are fetched by Gradle and retain their respective upstream licenses. The MIT license here does not relicense them.

The PhotonLib dependency is pinned to v2026.3.4 using the official release vendor manifest. Its upstream update URL can point to a different season; review version compatibility before updating dependencies.
