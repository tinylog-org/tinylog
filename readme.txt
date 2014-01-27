Copyright 2012 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.


===========
 Projects
===========

benchmark:
	Contains a benchmark with an ANT script for multiple logging frameworks

runtime-environment-stubs:
	Contains non-standard VM APIs (e.g. for Android)

log4j-facade:
	Contains the log4j facade (Log4j 1.x compatible logging API.)

tests:
	Contains all JUnit tests

tinylog:
	Contains tinylog and the ANT script to build tinylog

All projects can be imported as Eclipse Java project.


===============
 Build tinylog
===============

Run the ANT script "build.xml", which can be found in the "tinylog" folder.
Afterwards the created JARs can be found under "dist".

tinylog is compatible with Java 5 (and higher). Therefore it is recommend to
build tinylog with the run time classes of Java 5. The path to "rt.jar" can be
set optionally by the property "java5.boot.classpath".
