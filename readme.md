tinylog 3
=========
[![Build](https://github.com/tinylog-org/tinylog/actions/workflows/build.yaml/badge.svg?branch=v3.0&event=push)](https://github.com/tinylog-org/tinylog/actions/workflows/build.yaml)
[![Code Coverage](https://codecov.io/gh/tinylog-org/tinylog/branch/v3.0/graph/badge.svg)](https://codecov.io/gh/tinylog-org/tinylog/branch/v3.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog-api/badge.svg)](https://search.maven.org/search?q=g:org.tinylog)
[![Percentage of issues still open](https://isitmaintained.com/badge/open/tinylog-org/tinylog.svg)](https://github.com/tinylog-org/tinylog/issues "Percentage of issues still open")
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/tinylog-org/tinylog.svg)](https://github.com/tinylog-org/tinylog/issues "Average time to resolve an issue")

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

Outputting log entries to the console and into a log file via *tinylog.properties*

```properties
level           = INFO

writer1         = console
writer1.pattern = {date: HH:mm:ss.SSS} {class}.{method}() {level}: {message}

writer2         = file
writer2.file    = logs/myapp.log
writer2.pattern = {date: HH:mm:ss} [{thread}] {level}: {message}
```

Contributing
------------

On GitHub, issues and pull requests are always welcome :)

For building tinylog or contributing to this project, please take a look at [contributing.md](./contributing.md).

License
-------

Copyright 2020-2022 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
