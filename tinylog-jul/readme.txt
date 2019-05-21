Copyright 2019 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

------------------------------------------
   Using tinylog with java.util.logging
------------------------------------------

tinylog's logging API can be used on any web and application server that uses
java.util.logging as central logging engine. tinylog-jul.jar simply forwards all
log entries to java.util.logging.

The following JARs are required in the classpath:
  
  - tinylog-api.jar
    
  - tinylog-jul.jar

If you want to create a separate log for your Web App only in addition to the
central server log, you can also add tinylog-impl.jar. tinylog supports parallel
output via multiple logging back-ends.
