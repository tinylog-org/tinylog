tinylog 2
=========
[![Build Status](https://travis-ci.org/pmwmedia/tinylog.svg?branch=v2.0)](https://travis-ci.org/pmwmedia/tinylog)
[![Code Coverage](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0/graph/badge.svg)](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0)

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

Differences to tinylog 1.x
--------------------------

**Changes**

* Separation of API and implementation (tinylog-api.jar and tinylog-impl.jar)
* Compiled with debug information as it is nowadays common
* Root package is `org.tinylog` instead of `org.pmw.tinylog`
* [Logger](tinylog-api/src/main/java/org/tinylog/Logger.java) class contains only methods relevant for logging
* Properties are set without `tinylog.` prefix in `tinylog.properties` (for example: just `level` instead of `tinylog.level`)
* Logcat writer is the default writer on Android

**New features**

* Log entries can be optionally [tagged](tinylog-api/src/main/java/org/tinylog/Logger.java#L53)
  * Tags can be output via `{tag}` placeholder
  * A writer can be bound to a tag via the property `writer.tag`
* Multiple logging implementations can be combined (log entries can be redirected to the logging system of the application server and additionally be written to a separate log file by tinylog itself)
* Charset can be defined for file based writer (for example: `writer.charset = UTF-8`)

**Dropped features**

* Fluent-API (The recommended way to configure tinylog are properties files. Nevertheless the configuration can be changed or set programmatically via the [Configuration](tinylog-api/src/main/java/org/tinylog/configuration/Configuration.java) class.)
* Mutable configurations (For performance reasons, the configuration becomes immutable, when the first logging method is called.)

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
