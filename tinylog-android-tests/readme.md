# Android Tests

The JUnit tests can be executed on any Android virtual device with API level 26 and higher. The virtual device has to be
launched before executing the tests.

Gradle command for executing the JUnit tests including code coverage report:

```
gradlew createDebugCoverageReport spotbugsDebug checkstyle
```

It is recommended to use the latest version of [Android Studio](https://developer.android.com/studio) for launching the
virtual device and executing the Gradle command.
