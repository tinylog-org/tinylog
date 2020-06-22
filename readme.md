tinylog 2
=========
[![Build Status](https://travis-ci.org/pmwmedia/tinylog.svg?branch=v2.2)](https://travis-ci.org/pmwmedia/tinylog)
[![Code Coverage](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.2/graph/badge.svg)](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.2)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog-impl/badge.svg)](https://search.maven.org/search?q=g:org.tinylog)

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

More information about tinylog including a detailed user manual and the Javadoc documentation can be found on https://tinylog.org/v2/.

On GitHub, issues and pull requests are always welcome :)

Build tinylog
-------------

tinylog requires Maven 3.5 and JDK 10 for building. Newer JDKs cannot compile legacy code for older Java versions, and older JDKs cannot compile new features for the latest Java versions.

Build command:

	mvn clean install

A new folder "target" with Javadoc documentation and all JARs will be created in the root directory. The generated JARs contain Java 6 byte code and are compatible with any JRE 7 and higher as well as with Android API level 1 and higher.

License
-------

Copyright 2016-2020 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
