tinylog 2
=========
[![Build Status](https://travis-ci.org/pmwmedia/tinylog.svg?branch=v2.0)](https://travis-ci.org/pmwmedia/tinylog)
[![Code Coverage](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0/graph/badge.svg)](https://codecov.io/gh/pmwmedia/tinylog/branch/v2.0)

Example
-------

    import org.tinylog.Logger;
    
    public class Application {
    
        public static void main(String[] args) {
            Logger.info("Hello World!");
        }
    
    }

Build tinylog
-------------

tinylog can be built with Maven:

	mvn clean install

tinylog is compatible with Java 6 (and higher) but the JUnit tests require Java 8. Eclipse and IntelliJ have problems with different Java versions for main and test sources. Therefore Java 8 is defined for both in the parent POM and has to be overridden via system properties.

	"-Dbootclasspath=JAVA_6_HOME/lib/rt.jar"
	-Dmaven.compiler.source=1.6
	-Dmaven.compiler.target=1.6

License
-------

Copyright 2016 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
