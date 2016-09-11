tinylog
=======
[![Build Status](https://travis-ci.org/pmwmedia/tinylog.svg?branch=v1.2)](https://travis-ci.org/pmwmedia/tinylog)
[![Code Coverage](https://codecov.io/gh/pmwmedia/tinylog/branch/v1.2/graph/badge.svg)](https://codecov.io/gh/pmwmedia/tinylog/branch/v1.2)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.tinylog/tinylog)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.tinylog/tinylog/badge.svg)](http://www.javadoc.io/doc/org.tinylog/tinylog)

Example
-------

    import org.pmw.tinylog.Logger;
    
    public class Application {
    
        public static void main(String[] args) {
            Logger.info("Hello World!");
        }
    
    }

Projects
--------

* benchmark
  * Contains a benchmark for comparing logging frameworks
* jcl-binding:
  * Contains the Apache Commons Logging (JCL) binding, implementing the JCL logging API
* log4j-facade:
  * Contains the log4j facade, an Apache Log4j 1.x compatible logging API replacement
* slf4j-binding:
  * Contains the SLF4J binding, implementing the SLF4J logging API
* tinylog-core
  * Contains shared basis for tinylog and compatible server replacements
* tinylog-jboss
  * Contains tinylog API for JBoss Logging backend
* tinylog-jul
  * Contains tinylog API for java.util.logging backend
* tinylog
  * Contains tinylog itself

All projects can be imported as Maven projects.

Other folders
-------------
	
* configuration
  * Contains configuration files for Java formatter, Checkstyle and FindBugs

Support
-------

A detailed user manual and the Javadoc documentation can be found on http://www.tinylog.org/. Bug reports and feature requests are welcome and can be created via [GitHub issues](https://github.com/pmwmedia/tinylog/issues).

Build tinylog
-------------

tinylog can be built with Maven:

	mvn clean checkstyle:checkstyle findbugs:findbugs install
	
tinylog is compatible with Java 5 (and higher). Therefore it is recommend to build tinylog at least once against a Java 5 runtime before commiting something. The path to "rt.jar" as well as the source and target version can be set via properties.

	"-Dbootclasspath=JAVA_5_HOME/lib/rt.jar"
	-Dmaven.compiler.source=1.5
	-Dmaven.compiler.target=1.5

A new folder "target" with Javadoc documentation and all JARs will be created in the root directory.

License
-------

Copyright 2012 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
