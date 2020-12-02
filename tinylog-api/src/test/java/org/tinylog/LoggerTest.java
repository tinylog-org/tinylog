package org.tinylog;

import java.util.function.Supplier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.runtime.JavaIndexBasedStackTraceLocation;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class LoggerTest {

	private static MockedStatic<Tinylog> tinylogMock;
	private static LoggingBackend backend;
	private static LevelVisibility visibility;

	/**
	 * Initializes all mocks.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@BeforeAll
	static void create() {
		tinylogMock = mockStatic(Tinylog.class);
		backend = mock(LoggingBackend.class);
		visibility = mock(LevelVisibility.class);

		tinylogMock.when(Tinylog::getFramework).thenReturn(new Framework(false, false) {
			@Override
			public LoggingBackend getLoggingBackend() {
				return backend;
			}
		});
	}

	/**
	 * Restores the mocked tinylog class.
	 */
	@AfterAll
	static void dispose() {
		tinylogMock.close();
	}

	/**
	 * Initializes the logging backend mock.
	 */
	@BeforeEach
	void init() {
		when(backend.getLevelVisibility(null)).thenReturn(visibility);
	}

	/**
	 * Resets the logging backend and level visibility mocks.
	 */
	@AfterEach
	void reset() {
		Mockito.reset(backend, visibility);
	}

	/**
	 * Tests for category tests.
	 */
	@Nested
	class Tags {

		/**
		 * Verifies that the same logger instance is returned for the same tag.
		 */
		@Test
		void sameLoggerInstanceForSameTag() {
			TaggedLogger first = Logger.tag("foo");
			TaggedLogger second = Logger.tag("foo");
			assertThat(first).isNotNull().isSameAs(second);
		}

		/**
		 * Verifies that different logger instances are returned for different tags.
		 */
		@Test
		void differentLoggerInstanceForDifferentTag() {
			TaggedLogger first = Logger.tag("foo");
			TaggedLogger second = Logger.tag("boo");

			assertThat(first).isNotNull();
			assertThat(second).isNotNull();
			assertThat(first).isNotSameAs(second);
		}

		/**
		 * Verifies that the same untagged root logger is returned for {@code null} and empty tags.
		 */
		@Test
		void sameUntaggedRootLoggerForNullAndEmptyTags() {
			TaggedLogger nullTag = Logger.tag(null);
			TaggedLogger emptyTag = Logger.tag("");

			assertThat(nullTag).isNotNull();
			assertThat(nullTag.getTag()).isNull();
			assertThat(emptyTag).isNotNull();
			assertThat(emptyTag.getTag()).isNull();

			assertThat(nullTag).isSameAs(emptyTag);
		}

	}

	/**
	 * Tests for severity levels.
	 */
	@Nested
	class Levels {

		/**
		 * Verifies the results of the {@link Logger#isTraceEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isTraceEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isTraceEnabled(boolean visible, boolean enabled) {
			when(visibility.isTraceEnabled()).thenReturn(visible);
			lenient().when(backend.isEnabled(notNull(), isNull(), eq(Level.TRACE))).thenReturn(enabled);

			assertThat(Logger.isTraceEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link Logger#isDebugEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isDebugEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isDebugEnabled(boolean visible, boolean enabled) {
			when(visibility.isDebugEnabled()).thenReturn(visible);
			lenient().when(backend.isEnabled(notNull(), isNull(), eq(Level.DEBUG))).thenReturn(enabled);

			assertThat(Logger.isDebugEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link Logger#isInfoEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isInfoEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isInfoEnabled(boolean visible, boolean enabled) {
			when(visibility.isInfoEnabled()).thenReturn(visible);
			lenient().when(backend.isEnabled(notNull(), isNull(), eq(Level.INFO))).thenReturn(enabled);

			assertThat(Logger.isInfoEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link Logger#isWarnEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isWarnEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isWarnEnabled(boolean visible, boolean enabled) {
			when(visibility.isWarnEnabled()).thenReturn(visible);
			lenient().when(backend.isEnabled(notNull(), isNull(), eq(Level.WARN))).thenReturn(enabled);

			assertThat(Logger.isWarnEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link Logger#isErrorEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isErrorEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isErrorEnabled(boolean visible, boolean enabled) {
			when(visibility.isErrorEnabled()).thenReturn(visible);
			lenient().when(backend.isEnabled(notNull(), isNull(), eq(Level.ERROR))).thenReturn(enabled);

			assertThat(Logger.isErrorEnabled()).isEqualTo(visible && enabled);
		}

	}

	/**
	 * Tests for issuing log entries.
	 */
	@Nested
	class LogEntries {

		/**
		 * Tests issuing log entries if the assigned severity level is enabled.
		 */
		@Nested
		class Enabled {

			/**
			 * Verifies that a trace log entry with a plain text message can be issued.
			 */
			@Test
			void traceTextMessage() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Logger.trace("Hello World!");

				verifyLogEntry(Level.TRACE, null, "Hello World!");
			}

			/**
			 * Verifies that a trace log entry with an object can be issued.
			 */
			@Test
			void traceMessageObject() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Logger.trace(42);

				verifyLogEntry(Level.TRACE, null, 42);
			}

			/**
			 * Verifies that a trace log entry with a lazy text message can be issued.
			 */
			@Test
			void traceLazyMessage() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Hello World!";
				Logger.trace(supplier);

				verifyLogEntry(Level.TRACE, null, supplier);
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders can be issued.
			 */
			@Test
			void traceFormattedMessageWithArgument() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Logger.trace("Hello {}!", "Alice");

				verifyLogEntry(Level.TRACE, null, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@Test
			void traceFormattedMessageWithLazyArgument() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Alice";
				Logger.trace("Hello {}!", supplier);

				verifyLogEntry(Level.TRACE, null, "Hello {}!", supplier);
			}

			/**
			 * Verifies that a trace log entry with an exception can be issued.
			 */
			@Test
			void traceException() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.trace(exception);

				verifyLogEntry(Level.TRACE, exception, null);
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message can be issued.
			 */
			@Test
			void traceExceptionAndTextMessage() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.trace(exception, "Oops!");

				verifyLogEntry(Level.TRACE, exception, "Oops!");
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message can be issued.
			 */
			@Test
			void traceExceptionAndLazyMessage() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Oops!";
				Logger.trace(exception, supplier);

				verifyLogEntry(Level.TRACE, exception, supplier);
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
			 */
			@Test
			void traceExceptionAndFormattedMessageWithArgument() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.trace(exception, "Hello {}!", "Alice");

				verifyLogEntry(Level.TRACE, exception, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@Test
			void traceExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isTraceEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Alice";
				Logger.trace(exception, "Hello {}!", supplier);

				verifyLogEntry(Level.TRACE, exception, "Hello {}!", supplier);
			}

			/**
			 * Verifies that a debug log entry with a plain text message can be issued.
			 */
			@Test
			void debugTextMessage() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Logger.debug("Hello World!");

				verifyLogEntry(Level.DEBUG, null, "Hello World!");
			}

			/**
			 * Verifies that a debug log entry with an object can be issued.
			 */
			@Test
			void debugMessageObject() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Logger.debug(42);

				verifyLogEntry(Level.DEBUG, null, 42);
			}

			/**
			 * Verifies that a debug log entry with a lazy text message can be issued.
			 */
			@Test
			void debugLazyMessage() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Hello World!";
				Logger.debug(supplier);

				verifyLogEntry(Level.DEBUG, null, supplier);
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders can be issued.
			 */
			@Test
			void debugFormattedMessageWithArgument() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Logger.debug("Hello {}!", "Alice");

				verifyLogEntry(Level.DEBUG, null, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@Test
			void debugFormattedMessageWithLazyArgument() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Alice";
				Logger.debug("Hello {}!", supplier);

				verifyLogEntry(Level.DEBUG, null, "Hello {}!", supplier);
			}

			/**
			 * Verifies that a debug log entry with an exception can be issued.
			 */
			@Test
			void debugException() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.debug(exception);

				verifyLogEntry(Level.DEBUG, exception, null);
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message can be issued.
			 */
			@Test
			void debugExceptionAndTextMessage() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.debug(exception, "Oops!");

				verifyLogEntry(Level.DEBUG, exception, "Oops!");
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message can be issued.
			 */
			@Test
			void debugExceptionAndLazyMessage() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Oops!";
				Logger.debug(exception, supplier);

				verifyLogEntry(Level.DEBUG, exception, supplier);
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
			 */
			@Test
			void debugExceptionAndFormattedMessageWithArgument() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.debug(exception, "Hello {}!", "Alice");

				verifyLogEntry(Level.DEBUG, exception, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@Test
			void debugExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isDebugEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Alice";
				Logger.debug(exception, "Hello {}!", supplier);

				verifyLogEntry(Level.DEBUG, exception, "Hello {}!", supplier);
			}

			/**
			 * Verifies that an info log entry with a plain text message can be issued.
			 */
			@Test
			void infoTextMessage() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Logger.info("Hello World!");

				verifyLogEntry(Level.INFO, null, "Hello World!");
			}

			/**
			 * Verifies that an info log entry with an object can be issued.
			 */
			@Test
			void infoMessageObject() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Logger.info(42);

				verifyLogEntry(Level.INFO, null, 42);
			}

			/**
			 * Verifies that an info log entry with a lazy text message can be issued.
			 */
			@Test
			void infoLazyMessage() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Hello World!";
				Logger.info(supplier);

				verifyLogEntry(Level.INFO, null, supplier);
			}

			/**
			 * Verifies that an info log entry with a message with placeholders can be issued.
			 */
			@Test
			void infoFormattedMessageWithArgument() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Logger.info("Hello {}!", "Alice");

				verifyLogEntry(Level.INFO, null, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@Test
			void infoFormattedMessageWithLazyArgument() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Alice";
				Logger.info("Hello {}!", supplier);

				verifyLogEntry(Level.INFO, null, "Hello {}!", supplier);
			}

			/**
			 * Verifies that an info log entry with an exception can be issued.
			 */
			@Test
			void infoException() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.info(exception);

				verifyLogEntry(Level.INFO, exception, null);
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message can be issued.
			 */
			@Test
			void infoExceptionAndTextMessage() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.info(exception, "Oops!");

				verifyLogEntry(Level.INFO, exception, "Oops!");
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message can be issued.
			 */
			@Test
			void infoExceptionAndLazyMessage() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Oops!";
				Logger.info(exception, supplier);

				verifyLogEntry(Level.INFO, exception, supplier);
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders can be issued.
			 */
			@Test
			void infoExceptionAndFormattedMessageWithArgument() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.info(exception, "Hello {}!", "Alice");

				verifyLogEntry(Level.INFO, exception, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@Test
			void infoExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isInfoEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Alice";
				Logger.info(exception, "Hello {}!", supplier);

				verifyLogEntry(Level.INFO, exception, "Hello {}!", supplier);
			}

			/**
			 * Verifies that a warning log entry with a plain text message can be issued.
			 */
			@Test
			void warnTextMessage() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Logger.warn("Hello World!");

				verifyLogEntry(Level.WARN, null, "Hello World!");
			}

			/**
			 * Verifies that a warning log entry with an object can be issued.
			 */
			@Test
			void warnMessageObject() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Logger.warn(42);

				verifyLogEntry(Level.WARN, null, 42);
			}

			/**
			 * Verifies that a warning log entry with a lazy text message can be issued.
			 */
			@Test
			void warnLazyMessage() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Hello World!";
				Logger.warn(supplier);

				verifyLogEntry(Level.WARN, null, supplier);
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders can be issued.
			 */
			@Test
			void warnFormattedMessageWithArgument() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Logger.warn("Hello {}!", "Alice");

				verifyLogEntry(Level.WARN, null, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@Test
			void warnFormattedMessageWithLazyArgument() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Alice";
				Logger.warn("Hello {}!", supplier);

				verifyLogEntry(Level.WARN, null, "Hello {}!", supplier);
			}

			/**
			 * Verifies that a warning log entry with an exception can be issued.
			 */
			@Test
			void warnException() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.warn(exception);

				verifyLogEntry(Level.WARN, exception, null);
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message can be issued.
			 */
			@Test
			void warnExceptionAndTextMessage() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.warn(exception, "Oops!");

				verifyLogEntry(Level.WARN, exception, "Oops!");
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message can be issued.
			 */
			@Test
			void warnExceptionAndLazyMessage() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Oops!";
				Logger.warn(exception, supplier);

				verifyLogEntry(Level.WARN, exception, supplier);
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
			 */
			@Test
			void warnExceptionAndFormattedMessageWithArgument() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.warn(exception, "Hello {}!", "Alice");

				verifyLogEntry(Level.WARN, exception, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments
			 * can be issued.
			 */
			@Test
			void warnExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isWarnEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Alice";
				Logger.warn(exception, "Hello {}!", supplier);

				verifyLogEntry(Level.WARN, exception, "Hello {}!", supplier);
			}

			/**
			 * Verifies that an error log entry with a plain text message can be issued.
			 */
			@Test
			void errorTextMessage() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Logger.error("Hello World!");

				verifyLogEntry(Level.ERROR, null, "Hello World!");
			}

			/**
			 * Verifies that an error log entry with an object can be issued.
			 */
			@Test
			void errorMessageObject() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Logger.error(42);

				verifyLogEntry(Level.ERROR, null, 42);
			}

			/**
			 * Verifies that an error log entry with a lazy text message can be issued.
			 */
			@Test
			void errorLazyMessage() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Hello World!";
				Logger.error(supplier);

				verifyLogEntry(Level.ERROR, null, supplier);
			}

			/**
			 * Verifies that an error log entry with a message with placeholders can be issued.
			 */
			@Test
			void errorFormattedMessageWithArgument() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Logger.error("Hello {}!", "Alice");

				verifyLogEntry(Level.ERROR, null, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@Test
			void errorFormattedMessageWithLazyArgument() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Supplier<String> supplier = () -> "Alice";
				Logger.error("Hello {}!", supplier);

				verifyLogEntry(Level.ERROR, null, "Hello {}!", supplier);
			}

			/**
			 * Verifies that an error log entry with an exception can be issued.
			 */
			@Test
			void errorException() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.error(exception);

				verifyLogEntry(Level.ERROR, exception, null);
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message can be issued.
			 */
			@Test
			void errorExceptionAndTextMessage() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.error(exception, "Oops!");

				verifyLogEntry(Level.ERROR, exception, "Oops!");
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message can be issued.
			 */
			@Test
			void errorExceptionAndLazyMessage() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Oops!";
				Logger.error(exception, supplier);

				verifyLogEntry(Level.ERROR, exception, supplier);
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders can be issued.
			 */
			@Test
			void errorExceptionAndFormattedMessageWithArgument() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Logger.error(exception, "Hello {}!", "Alice");

				verifyLogEntry(Level.ERROR, exception, "Hello {}!", "Alice");
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@Test
			void errorExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isErrorEnabled()).thenReturn(true);

				Exception exception = new Exception();
				Supplier<String> supplier = () -> "Alice";
				Logger.error(exception, "Hello {}!", supplier);

				verifyLogEntry(Level.ERROR, exception, "Hello {}!", supplier);
			}

			/**
			 * Verifies backend mock invocation with expected log entry values.
			 *
			 * @param level     Severity level
			 * @param exception Exception or any other kind of throwable
			 * @param message   Message object
			 * @param arguments Optional arguments
			 */
			private void verifyLogEntry(Level level, Throwable exception, Object message, Object... arguments) {
				verify(backend, atMostOnce()).getLevelVisibility(null);

				verify(backend).log(
					isA(JavaIndexBasedStackTraceLocation.class),
					isNull(),
					same(level),
					same(exception),
					same(message),
					arguments.length == 0 ? isNull() : eq(arguments),
					arguments.length == 0 ? isNull() : isA(EnhancedMessageFormatter.class)
				);
			}

		}

		/**
		 * Tests discarding log entries if the assigned severity level is disabled.
		 */
		@Nested
		class Disabled {

			/**
			 * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
			 * disabled.
			 */
			@Test
			void traceTextMessage() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace("Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an object is discarded if the trace severity level is disabled.
			 */
			@Test
			void traceMessageObject() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(42);
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
			 * disabled.
			 */
			@Test
			void traceLazyMessage() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(() -> "Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
			 * is disabled.
			 */
			@Test
			void traceFormattedMessageWithArgument() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace("Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
			 * trace severity level is disabled.
			 */
			@Test
			void traceFormattedMessageWithLazyArgument() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace("Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
			 */
			@Test
			void traceException() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(new Exception());
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
			 * severity level is disabled.
			 */
			@Test
			void traceExceptionAndTextMessage() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(new Exception(), "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
			 * severity level is disabled.
			 */
			@Test
			void traceExceptionAndLazyMessage() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(new Exception(), () -> "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
			 * trace severity level is disabled.
			 */
			@Test
			void traceExceptionAndFormattedMessageWithArgument() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(new Exception(), "Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the trace severity level is disabled.
			 */
			@Test
			void traceExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isTraceEnabled()).thenReturn(false);

				Logger.trace(new Exception(), "Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
			 * disabled.
			 */
			@Test
			void debugTextMessage() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug("Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
			 */
			@Test
			void debugMessageObject() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(42);
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
			 * disabled.
			 */
			@Test
			void debugLazyMessage() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(() -> "Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
			 * is disabled.
			 */
			@Test
			void debugFormattedMessageWithArgument() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug("Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
			 * debug severity level is disabled.
			 */
			@Test
			void debugFormattedMessageWithLazyArgument() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug("Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
			 */
			@Test
			void debugException() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(new Exception());
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
			 * severity level is disabled.
			 */
			@Test
			void debugExceptionAndTextMessage() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(new Exception(), "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
			 * severity level is disabled.
			 */
			@Test
			void debugExceptionAndLazyMessage() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(new Exception(), () -> "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
			 * debug severity level is disabled.
			 */
			@Test
			void debugExceptionAndFormattedMessageWithArgument() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(new Exception(), "Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the debug severity level is disabled.
			 */
			@Test
			void debugExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isDebugEnabled()).thenReturn(false);

				Logger.debug(new Exception(), "Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with a plain text message is discarded if the info severity level is
			 * disabled.
			 */
			@Test
			void infoTextMessage() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info("Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
			 */
			@Test
			void infoMessageObject() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(42);
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
			 * disabled.
			 */
			@Test
			void infoLazyMessage() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(() -> "Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
			 * is disabled.
			 */
			@Test
			void infoFormattedMessageWithArgument() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info("Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
			 * info severity level is disabled.
			 */
			@Test
			void infoFormattedMessageWithLazyArgument() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info("Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
			 */
			@Test
			void infoException() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(new Exception());
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message is discarded if the info
			 * severity level is disabled.
			 */
			@Test
			void infoExceptionAndTextMessage() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(new Exception(), "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
			 * severity level is disabled.
			 */
			@Test
			void infoExceptionAndLazyMessage() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(new Exception(), () -> "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
			 * info severity level is disabled.
			 */
			@Test
			void infoExceptionAndFormattedMessageWithArgument() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(new Exception(), "Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the info severity level is disabled.
			 */
			@Test
			void infoExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isInfoEnabled()).thenReturn(false);

				Logger.info(new Exception(), "Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
			 * disabled.
			 */
			@Test
			void warnTextMessage() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn("Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
			 */
			@Test
			void warnMessageObject() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(42);
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
			 * disabled.
			 */
			@Test
			void warnLazyMessage() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(() -> "Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity
			 * level is disabled.
			 */
			@Test
			void warnFormattedMessageWithArgument() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn("Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
			 * warn severity level is disabled.
			 */
			@Test
			void warnFormattedMessageWithLazyArgument() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn("Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
			 */
			@Test
			void warnException() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(new Exception());
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
			 * severity level is disabled.
			 */
			@Test
			void warnExceptionAndTextMessage() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(new Exception(), "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
			 * severity level is disabled.
			 */
			@Test
			void warnExceptionAndLazyMessage() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(new Exception(), () -> "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
			 * warn severity level is disabled.
			 */
			@Test
			void warnExceptionAndFormattedMessageWithArgument() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(new Exception(), "Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the warn severity level is disabled.
			 */
			@Test
			void warnExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isWarnEnabled()).thenReturn(false);

				Logger.warn(new Exception(), "Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with a plain text message is discarded if the error severity level is
			 * disabled.
			 */
			@Test
			void errorTextMessage() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error("Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
			 */
			@Test
			void errorMessageObject() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(42);
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
			 * disabled.
			 */
			@Test
			void errorLazyMessage() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(() -> "Hello World!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with a message with placeholders is discarded if the error severity
			 * level is disabled.
			 */
			@Test
			void errorFormattedMessageWithArgument() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error("Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
			 * error severity level is disabled.
			 */
			@Test
			void errorFormattedMessageWithLazyArgument() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error("Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
			 */
			@Test
			void errorException() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(new Exception());
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message is discarded if the error
			 * severity level is disabled.
			 */
			@Test
			void errorExceptionAndTextMessage() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(new Exception(), "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
			 * severity level is disabled.
			 */
			@Test
			void errorExceptionAndLazyMessage() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(new Exception(), () -> "Oops!");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
			 * error severity level is disabled.
			 */
			@Test
			void errorExceptionAndFormattedMessageWithArgument() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(new Exception(), "Hello {}!", "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the error severity level is disabled.
			 */
			@Test
			void errorExceptionAndFormattedMessageWithLazyArgument() {
				when(visibility.isErrorEnabled()).thenReturn(false);

				Logger.error(new Exception(), "Hello {}!", () -> "Alice");
				verifyNoLogEntry();
			}

			/**
			 * Verifies no invocations of logging methods for mocked backend.
			 */
			private void verifyNoLogEntry() {
				verify(backend, atMostOnce()).getLevelVisibility(null);
				verifyNoMoreInteractions(backend);
			}

		}

	}

}
