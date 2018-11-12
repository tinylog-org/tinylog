Copyright 2018 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

---------------------------
   Using tinylog 1.3 API
---------------------------

tinylog1.3-api.jar provides the logging API of tinylog 1.3 for tinylog 2 and
should be used instead of the old tinylog.jar. Thus, classes that still use
tinylog 1.3 for logging can be used together with tinylog 2.

The following JARs are required in the classpath:

  - tinylog1.3-api.jar
  
  - tinylog-api.jar (https://tinylog.org/v2/download/)
  
  - tinylog-impl.jar or any other implementation

Log entries are forwarded from tinylog1.3-api.jar to tinylog-api.jar and
processed by the tinylog 2 implementation like other log entries.
