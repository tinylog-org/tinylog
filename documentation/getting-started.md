# Getting Started

## Classpath and Dependencies

You can [download the latest version of tinylog](https://github.com/tinylog-org/tinylog/releases) and add the following JARs to your classpath:
- tinylog-api.jar
- tinylog-iml.jar
- tinylog-core.jar

Or you can add the latest version of tinylog as dependency for your favorite build tool:
```maven-pom (Maven (Default))
<dependency>
	<groupId>org.tinylog</groupId>
	<artifactId>tinylog-api</artifactId>
	<version>VERSION</version>
</dependency>
<dependency>
	<groupId>org.tinylog</groupId>
	<artifactId>tinylog-impl</artifactId>
	<version>VERSION</version>
</dependency>
```
```maven-pom (Maven (Kotlin))
<dependency>
	<groupId>org.tinylog</groupId>
	<artifactId>tinylog-api-kotlin</artifactId>
	<version>VERSION</version>
</dependency>
<dependency>
	<groupId>org.tinylog</groupId>
	<artifactId>tinylog-impl</artifactId>
	<version>VERSION</version>
</dependency>
```
```gradle (Gradle (Default))
implementation 'org.tinylog:tinylog-api:VERSION'
implementation 'org.tinylog:tinylog-impl:VERSION'
```
```kotlin (Gradle (Kotlin))
implementation("org.tinylog:tinylog-api-kotlin:VERSION")
implementation("org.tinylog:tinylog-impl:VERSION")
```
```plain (Eclipse P2)
https://tinylog.org/p2-repository/VERSION/
```

## Logging

Just add a logging statement to your application:

```java (Java)
import org.tinylog.Logger;

public class Application {

	public static void main(String[] args) {
		Logger.info("Hello World!");
	}

}
```
```kotlin (Kotlin)
import org.tinylog.kotlin.Logger

fun main() {
	Logger.info("Hello World!")
}
```

When you run this example application, you will see a log entry in the console:

```
2022-06-11 14:24:47 [main] INFO  Application.main(): Hello World!
```

# Configuration

You can configure tinylog by creating a properties file with the name `tinylog.properties` in the default classpath. If you use a build tool such as Maven or Gradle, it is usually located at `src/main/resources`. For plain IDE projects, it is usually located directly at `src` along with source files and packages.

Example `tinylog.properties`:

```properties
writer.type    = console
writer.pattern = {date: HH:mm:ss.SSS} {level}: {message}
```

When you run the previous example application now, you will see the log entry in the new format in the console:

```
14:24:47 INFO: Hello World!
```

For a detailed documentation of all configuration parameters, see the [configuration page](./configuration.md), and for all logging methods, see the [logging page](./logging.md).
