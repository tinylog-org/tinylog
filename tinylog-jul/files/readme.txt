Copyright 2016 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.


======================================
 Using tinylog on Glassfish or Tomcat
======================================

1) Add "tinylog-jul.jar" instead of "tinylog.jar" to your classpath

2) Optionally attach "tinylog-jul-javadoc.jar" for Javadoc documentation and
   "tinylog-jul-sources.jar" for source code attachment
   
3) Now you can test the logger in your web application:
   
      import org.pmw.tinylog.Logger;

      public void foo() {
         Logger.info("Hello World!");
      }

  All log entries will be redirected to java.util.logging.Logger. Logging can
  be configured through the global configuration files of your server.

4) A detailed user manual and the Javadoc documentation can be found on
   http://www.tinylog.org/
