# Contributing to tinylog

[Pull requests](https://github.com/tinylog-org/tinylog/pulls) are always welcome :) This document should help to build tinylog on your local machine. If you had a build problem nevertheless, you would be welcome to raise a [Github issue](https://github.com/tinylog-org/tinylog/issues/new?assignees=&labels=question&template=question.md&title=).

## Downloading JDK 9

A JDK 9 is required to build tinylog 2. Newer JDKs cannot compile legacy code for older Java versions, and older JDKs cannot compile new features for the latest Java versions.

OpenJDK 9 can be downloaded from [adoptopenjdk.net](https://adoptopenjdk.net/releases.html?variant=openjdk9&jvmVariant=hotspot) for Windows, MacOS, and Linux. The downloaded archive only needs to be unpacked. No installation is required.

## Building tinylog

tinylog 2 can be built with Maven 3.5 and later:

```
mvn clean verify
```

Before creating a pull request, please always ensure that this Maven command can be executed successfully. It compiles the code, runs JUnit tests, checks the code style via Checkstyle, and analyzes the code with SpotBugs.

## IDE support

In the [configuration folder](./configuration), there is an [Eclipse code formatter](./configuration/formatter.xml), which can be also imported in IntelliJ and VS Code. There are also [Checkstyle rules](./configuration/checkstyle-rules.xml), which can be used for the Checkstyle integrations of all popular IDEs for showing Checkstyle violations directly in the IDE while writing code.

## Source code

tinylog 2 supports any Java version from Java 6 and later. Therefore, all source code must be compatible with Java 6. For example, the usage of diamond operators and lambdas is not supported.

In general, tinylog uses standard Java source code style and Checkstyle will report and violations. However, please keep in mind to write JUnit tests for all new classes and methods and document them with Javadoc comments. JUnit tests for new code should test all code branches including corner cases, errors, and exception handling. A good code coverage is usually between 85% and 100%. Codecov will automatically post a code coverage report when creating or updating a pull request.

## Commits

When committing to tinylog, the subject line of the commit message should start with a verb, not end with any punctuation, and be no longer than 72 characters. If a more detailed message description is useful, it can be put after the subject line, separated by a blank line. For the optional detailed message description, there is no line length limit and it may contain one or more sentences. The associated issue or pull request can either be in parentheses at the end of the subject line or be part of the text body.

Good examples for commit messages:

```
Add automatic module names and reuse them for bundles (#110)
```

```
Add encoder runnable for compressing files by GZIP algorithm

See #139
```

```
Run Checkstyle and Findbugs during verify phase

This avoids side effects with instrumented classes from JaCoCo.
```
