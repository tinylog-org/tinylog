tinylog 2
=========
[![Build](https://github.com/tinylog-org/tinylog/actions/workflows/build.yaml/badge.svg?branch=v2.6&event=push)](https://github.com/tinylog-org/tinylog/actions/workflows/build.yaml)
[![Code Coverage](https://codecov.io/gh/tinylog-org/tinylog/branch/v2.6/graph/badge.svg)](https://codecov.io/gh/tinylog-org/tinylog/branch/v2.6)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog-impl/badge.svg)](https://search.maven.org/search?q=g:org.tinylog)
[![Percentage of issues still open](https://isitmaintained.com/badge/open/tinylog-org/tinylog.svg)](https://github.com/tinylog-org/tinylog/issues "Percentage of issues still open")

Example
-------

```java
import org.tinylog.Logger;
    
public class Application {

    public static void main(String[] args) {
        Logger.info("Hello {}!", "world");
    }

}
```

Outputting log entries to the console and rolling log files via *tinylog.properties*

```properties
level            = INFO

writer1          = console
writer1.format   = {date: HH:mm:ss.SSS} {class}.{method}() {level}: {message}

writer2          = rolling file
writer2.file     = logs/{date: yyyy-MM-dd}/log_{count}.txt
writer2.policies = startup, daily: 03:00
writer2.format   = {date: HH:mm:ss} [{thread}] {level}: {message}
```

More information about tinylog including a detailed user manual and the Javadoc documentation can be found on https://tinylog.org/v2/.

Contributing
------------

On GitHub, issues and pull requests are always welcome :)

For building tinylog or contributing to this project, please take a look at [contributing.md](./contributing.md).

License
-------

Copyright 2016-2022 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
