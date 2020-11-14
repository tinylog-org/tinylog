package org.tinylog.core.backend;

import javax.inject.Inject;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;
import org.tinylog.core.test.system.CaptureSystemOutput;
import org.tinylog.core.test.system.Output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@CaptureSystemOutput
class InternalLoggingBackendTest {
	
	@Inject
	private Output output;

	/**
	 * Verifies that all severity levels are disabled in the precalculated level visibility object for untagged log
	 * entries.
	 */
	@Test
	void untaggedVisibility() {
		LevelVisibility visibility = new InternalLoggingBackend().getLevelVisibility(null);

		AssertionsForClassTypes.assertThat(visibility.isTraceEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isDebugEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isInfoEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isWarnEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isErrorEnabled()).isFalse();
	}

	/**
	 * Verifies that only the warn and error severity levels are enabled in the precalculated level visibility object
	 * for internal tinylog log entries.
	 */
	@Test
	void tinylogVisibility() {
		LevelVisibility visibility = new InternalLoggingBackend().getLevelVisibility("tinylog");

		AssertionsForClassTypes.assertThat(visibility.isTraceEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isDebugEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isInfoEnabled()).isFalse();
		AssertionsForClassTypes.assertThat(visibility.isWarnEnabled()).isTrue();
		AssertionsForClassTypes.assertThat(visibility.isErrorEnabled()).isTrue();
	}

	/**
	 * Verifies that logging is disabled for untagged log entries at all severity levels.
	 *
	 * @param level The severity level to test
	 */
	@ParameterizedTest
	@EnumSource(Level.class)
	void untaggedLogEntriesDisabled(Level level) {
		InternalLoggingBackend backend = new InternalLoggingBackend();
		assertThat(backend.isEnabled(mock(StackTraceLocation.class), null, level)).isFalse();
	}

	/**
	 * Verifies that logging is disabled for tinylog log entries at trace, debug, and info severity levels.
	 *
	 * @param level The severity level to test
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"TRACE", "DEBUG", "INFO"})
	void tinylogLogEntriesDisabled(Level level) {
		InternalLoggingBackend backend = new InternalLoggingBackend();
		assertThat(backend.isEnabled(mock(StackTraceLocation.class), "tinylog", level)).isFalse();
	}

	/**
	 * Verifies that logging is enabled for tinylog log entries at warn and error severity levels.
	 *
	 * @param level The severity level to test
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	void tinylogLogEntriesEnabled(Level level) {
		InternalLoggingBackend backend = new InternalLoggingBackend();
		assertThat(backend.isEnabled(mock(StackTraceLocation.class), "tinylog", level)).isTrue();
	}

	/**
	 * Verifies that a plain text message can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	void plainTextMessage(Level level) {
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
	void formattedTextMessage(Level level) {
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
	void exceptionOnly(Level level) {
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
	void exceptionWithCustomMessage(Level level) {
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
	void discardNonServeLogEntries(Level level) {
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
	void discardNonTinylogLogEntries(String tag, Level level) {
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
