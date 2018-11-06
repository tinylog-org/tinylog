tinylog 2
=========
[![Build Status](https://travis-ci.org/pmwmedia/tinylog.svg?branch=v2.0)](https://travis-ci.org/pmwmedia/tinylog)
[![Code Coverage](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0/graph/badge.svg)](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog)

Example
-------

```java
import org.tinylog.Logger;
    
public class Application {

    public static void main(String[] args) {
        Logger.info("Hello World!");
    }

}
```

Support
-------

A detailed user manual and the Javadoc documentation can be found on https://tinylog.org/v2/. Bug reports and feature requests are welcome and can be created via [GitHub issues](https://github.com/pmwmedia/tinylog/issues).

Build tinylog
-------------

tinylog requires Maven 3 and JDK 9 or 10 for building. The generated JARs are compatible with Java 6 and higher.

Build command:

	mvn clean install -P release

A new folder "target" with Javadoc documentation and all JARs will be created in the root directory.

License
-------

Copyright 2016 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
