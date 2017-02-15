Copyright 2017 Martin Winandy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

-------------------
   Using tinylog
-------------------

1) Add "tinylog-api.jar" and "tinylog-impl.jar" to your classpath

2) Optionally attach "tinylog-api-javadoc.jar" and "tinylog-impl-javadoc.jar"
   for Javadoc documentation and/or "tinylog-api-sources.jar" and 
   "tinylog-impl-sources.jar" for source code attachment
   
3) Now you can test tinylog by the following simple program:

      import org.tinylog.Logger;

      public class Application {
         public static void main(String[] args) {
            Logger.info("Hello World!");
         }
      }

   After compilation, try it out by issuing the command

      java Application
  
   You should see a log statement appearing on the console.

4) A detailed user manual and the Javadoc documentation can be found on
   http://www.tinylog.org/
