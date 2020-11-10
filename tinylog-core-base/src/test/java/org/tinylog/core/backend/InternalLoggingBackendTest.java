package org.tinylog.core.backend;

import javax.inject.Inject;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.test.system.CaptureSystemOutput;
import org.tinylog.core.test.system.Output;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureSystemOutput
class InternalLoggingBackendTest {
	
	@Inject
	private Output output;

	/**
	 * Verifies that a plain text message can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void plainTextMessage(Level level) {
		new InternalLoggingBackend().log(
			null,
			"tinylog",
			level,
			null,
			"Hello World!",
			null,
			null
		);

		assertThat(output.consume()).containsExactly("TINYLOG " + level + ": Hello World!");
	}

	/**
	 * Verifies that a formatted text message with placeholders can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void formattedTextMessage(Level level) {
		new InternalLoggingBackend().log(
			null,
			"tinylog",
			level,
			null,
			"Hello {}!",
			new Object[] {"world"},
			new EnhancedMessageFormatter(new Framework(false, false))
		);

		assertThat(output.consume()).containsExactly("TINYLOG " + level + ": Hello world!");
	}

	/**
	 * Verifies that an exception can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void exceptionOnly(Level level) {
		Exception exception = new NullPointerException();
		exception.setStackTrace(new StackTraceElement[] {
			new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
			new StackTraceElement("example.OtherClass", "bar", "OtherClass.java", 42),
		});

		new InternalLoggingBackend().log(
			null,
			"tinylog",
			level,
			exception,
			null,
			null,
			null
		);

		assertThat(output.consume()).containsExactly(
			"TINYLOG " + level + ": java.lang.NullPointerException",
			"\tat example.MyClass.foo(MyClass.java:42)",
			"\tat example.OtherClass.bar(OtherClass.java:42)"
		);
	}

	/**
	 * Verifies that an exception can be output together with a custom message at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void exceptionWithCustomMessage(Level level) {
		Exception exception = new UnsupportedOperationException();
		exception.setStackTrace(new StackTraceElement[] {
			new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
		});

		new InternalLoggingBackend().log(
			null,
			"tinylog",
			level,
			exception,
			"Oops!",
			null,
			null
		);

		assertThat(output.consume()).containsExactly(
			"TINYLOG " + level + ": Oops!: java.lang.UnsupportedOperationException",
			"\tat example.MyClass.foo(MyClass.java:42)"
		);
	}

	/**
	 * Verifies that log entries are discarded for the severity levels trace, debug, and info.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"TRACE", "DEBUG", "INFO"})
	public void discardNonServeLogEntries(Level level) {
		new InternalLoggingBackend().log(
			null,
			"tinylog",
			level,
			null,
			"Hello World!",
			null,
			null
		);

		assertThat(output.consume()).isEmpty();
	}

	/**
	 * Verifies that log entries will be discarded, if tag is not {@code tinylog}.
	 *
	 * @param tag The category tag for the log entry
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@CsvSource({",ERROR", "foo,ERROR", ",WARN", "foo,WARN"})
	public void discardNonTinylogLogEntries(String tag, Level level) {
		new InternalLoggingBackend().log(
			null,
			tag,
			level,
			null,
			"Hello World!",
			null,
			null
		);

		assertThat(output.consume()).isEmpty();
	}

}
