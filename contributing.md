# Contributing to tinylog

[Pull requests](https://github.com/tinylog-org/tinylog/pulls) are always welcome :) This document should help to build tinylog on your local machine. If you have any build problem nevertheless, please feel free to report a [GitHub issue](https://github.com/tinylog-org/tinylog/issues/new?assignees=&labels=question&template=question.md&title=).

## Building tinylog

For compiling tinylog 3, a JDK 9 or later is required. It is recommended to use a JDK 9 or any later LTS version.

As build tool, Maven 3.5 or later is required. tinylog 3 can be built and installed in the local repository via the typical Maven command:

```
mvn clean install
```

Before creating a pull request, please always ensure that this Maven command `mvn verify` can be executed successfully. It compiles the code, runs JUnit tests, checks the code style via Checkstyle, and analyzes the code with SpotBugs.

## Source code

The compiled Maven artifacts of tinylog 3 support any Java version from Java 8 and above. Therefore, all source code must be compatible with Java 8.

In general, tinylog uses standard Java source code style and Checkstyle will report any violations. Please provide JUnit tests and Javadoc documentation for all new classes and methods. JUnit tests for new code should test all code branches including corner cases, errors, and exception handling. A good code coverage is usually between 80% and 100%. Codecov will automatically post a code coverage report when creating or updating a pull request.

## Commits

When committing to tinylog, the subject line of the commit message should start with a verb, not end with any punctuation, and be no longer than 72 characters. If a more detailed message description is useful, it can be put after the subject line, separated by a blank line. For the optional detailed message description, there is no line length limit, and it may contain one or more sentences. The associated issue or pull request can either be in parentheses at the end of the subject line or be part of the description body.

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
