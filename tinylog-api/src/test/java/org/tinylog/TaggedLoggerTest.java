package org.tinylog;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.runtime.StackTraceLocation;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.core.test.log.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class TaggedLoggerTest {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	/**
	 * Tests for category tags.
	 */
	@CaptureLogEntries
	@Nested
	class Tags {

		@Inject
		private Framework framework;

		/**
		 * Verifies that a string can be assigned as tag.
		 */
		@Test
		void stringTag() {
			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "dummy", framework);
			assertThat(logger.getTag()).isEqualTo("dummy");
		}

		/**
		 * Verifies that {@code null} can be passed as tag for creating an untagged logger.
		 */
		@Test
		void nullTag() {
			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, null, framework);
			assertThat(logger.getTag()).isNull();
		}

	}

	/**
	 * Tests for severity levels.
	 */
	@ExtendWith(MockitoExtension.class)
	@Nested
	class Levels {

		@Mock
		private LoggingBackend backend;

		private final Framework framework = new Framework(false, false) {

			@Override
			public LoggingBackend getLoggingBackend() {
				return backend;
			}

		};

		/**
		 * Verifies the results of the {@link TaggedLogger#isTraceEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isTraceEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource({"false,false", "false,true", "true,false", "true,true"})
		void isTraceEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(visible, false, false, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.TRACE))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "test", framework);
			assertThat(logger.isTraceEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isDebugEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isDebugEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource({"false,false", "false,true", "true,false", "true,true"})
		void isDebugEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, visible, false, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.DEBUG))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "test", framework);
			assertThat(logger.isDebugEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isInfoEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isInfoEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource({"false,false", "false,true", "true,false", "true,true"})
		void isInfoEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, visible, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.INFO))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "test", framework);
			assertThat(logger.isInfoEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isWarnEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isWarnEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource({"false,false", "false,true", "true,false", "true,true"})
		void isWarnEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, true, visible, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.WARN))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "test", framework);
			assertThat(logger.isWarnEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isErrorEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isErrorEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource({"false,false", "false,true", "true,false", "true,true"})
		void isErrorEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, true, true, visible)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.ERROR))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, "test", framework);
			assertThat(logger.isErrorEnabled()).isEqualTo(visible && enabled);
		}

	}

	/**
	 * Tests for issuing log entries.
	 */
	@Nested
	class LogEntries {

		private static final String TAG = "test";

		@Inject
		private Framework framework;

		@Inject
		private Log log;

		private TaggedLogger logger;

		/**
		 * Creates the tagged logger instance and clears all trace and debug log entries, which have been issued while
		 * the creation.
		 */
		@BeforeEach
		void init() {
			logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH, TAG, framework);
			assertThat(log.consume())
				.allSatisfy(entry -> assertThat(entry.getLevel()).isGreaterThanOrEqualTo(Level.DEBUG));
		}

		/**
		 * Tests issuing log entries if the assigned severity level is enabled.
		 */
		@Nested
		class Enabled {

			/**
			 * Verifies that a trace log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceTextMessage() {
				logger.trace("Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"));
			}

			/**
			 * Verifies that a trace log entry with an object can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceMessageObject() {
				logger.trace(42);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "42"));
			}

			/**
			 * Verifies that a trace log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceLazyMessage() {
				logger.trace(() -> "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"));
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceFormattedMessageWithArgument() {
				logger.trace("Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceFormattedMessageWithLazyArgument() {
				logger.trace("Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a trace log entry with an exception can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceException() {
				Exception exception = new Exception();
				logger.trace(exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, null));
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceExceptionAndTextMessage() {
				Exception exception = new Exception();
				logger.trace(exception, "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"));
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceExceptionAndLazyMessage() {
				Exception exception = new Exception();
				logger.trace(exception, () -> "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"));
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceExceptionAndFormattedMessageWithArgument() {
				Exception exception = new Exception();
				logger.trace(exception, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(minLevel = Level.TRACE)
			@Test
			void traceExceptionAndFormattedMessageWithLazyArgument() {
				Exception exception = new Exception();
				logger.trace(exception, "Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that a debug log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugTextMessage() {
				logger.debug("Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"));
			}

			/**
			 * Verifies that a debug log entry with an object can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugMessageObject() {
				logger.debug(42);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "42"));
			}

			/**
			 * Verifies that a debug log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugLazyMessage() {
				logger.debug(() -> "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"));
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugFormattedMessageWithArgument() {
				logger.debug("Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugFormattedMessageWithLazyArgument() {
				logger.debug("Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a debug log entry with an exception can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugException() {
				Exception exception = new Exception();
				logger.debug(exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, null));
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugExceptionAndTextMessage() {
				Exception exception = new Exception();
				logger.debug(exception, "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"));
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugExceptionAndLazyMessage() {
				Exception exception = new Exception();
				logger.debug(exception, () -> "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"));
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugExceptionAndFormattedMessageWithArgument() {
				Exception exception = new Exception();
				logger.debug(exception, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void debugExceptionAndFormattedMessageWithLazyArgument() {
				Exception exception = new Exception();
				logger.debug(exception, "Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that an info log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoTextMessage() {
				logger.info("Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello World!"));
			}

			/**
			 * Verifies that an info log entry with an object can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoMessageObject() {
				logger.info(42);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "42"));
			}

			/**
			 * Verifies that an info log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoLazyMessage() {
				logger.info(() -> "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello World!"));
			}

			/**
			 * Verifies that an info log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoFormattedMessageWithArgument() {
				logger.info("Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"));
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoFormattedMessageWithLazyArgument() {
				logger.info("Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"));
			}

			/**
			 * Verifies that an info log entry with an exception can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoException() {
				Exception exception = new Exception();
				logger.info(exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, null));
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoExceptionAndTextMessage() {
				Exception exception = new Exception();
				logger.info(exception, "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Oops!"));
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoExceptionAndLazyMessage() {
				Exception exception = new Exception();
				logger.info(exception, () -> "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Oops!"));
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoExceptionAndFormattedMessageWithArgument() {
				Exception exception = new Exception();
				logger.info(exception, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void infoExceptionAndFormattedMessageWithLazyArgument() {
				Exception exception = new Exception();
				logger.info(exception, "Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that a warning log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnTextMessage() {
				logger.warn("Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello World!"));
			}

			/**
			 * Verifies that a warning log entry with an object can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnMessageObject() {
				logger.warn(42);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "42"));
			}

			/**
			 * Verifies that a warning log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnLazyMessage() {
				logger.warn(() -> "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello World!"));
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnFormattedMessageWithArgument() {
				logger.warn("Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnFormattedMessageWithLazyArgument() {
				logger.warn("Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"));
			}

			/**
			 * Verifies that a warning log entry with an exception can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnException() {
				Exception exception = new Exception();
				logger.warn(exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, null));
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnExceptionAndTextMessage() {
				Exception exception = new Exception();
				logger.warn(exception, "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Oops!"));
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnExceptionAndLazyMessage() {
				Exception exception = new Exception();
				logger.warn(exception, () -> "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Oops!"));
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnExceptionAndFormattedMessageWithArgument() {
				Exception exception = new Exception();
				logger.warn(exception, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments
			 * can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void warnExceptionAndFormattedMessageWithLazyArgument() {
				Exception exception = new Exception();
				logger.warn(exception, "Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that an error log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorTextMessage() {
				logger.error("Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"));
			}

			/**
			 * Verifies that an error log entry with an object can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorMessageObject() {
				logger.error(42);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "42"));
			}

			/**
			 * Verifies that an error log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorLazyMessage() {
				logger.error(() -> "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"));
			}

			/**
			 * Verifies that an error log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorFormattedMessageWithArgument() {
				logger.error("Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"));
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorFormattedMessageWithLazyArgument() {
				logger.error("Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"));
			}

			/**
			 * Verifies that an error log entry with an exception can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorException() {
				Exception exception = new Exception();
				logger.error(exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, null));
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorExceptionAndTextMessage() {
				Exception exception = new Exception();
				logger.error(exception, "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"));
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorExceptionAndLazyMessage() {
				Exception exception = new Exception();
				logger.error(exception, () -> "Oops!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"));
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorExceptionAndFormattedMessageWithArgument() {
				Exception exception = new Exception();
				logger.error(exception, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Hello Alice!"));
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void errorExceptionAndFormattedMessageWithLazyArgument() {
				Exception exception = new Exception();
				logger.error(exception, "Hello {}!", () -> "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Hello Alice!"));
			}

			/**
			 * Creates a new log entry.
			 *
			 * @param level     Severity level
			 * @param exception Exception or any other kind of throwable
			 * @param message   Text message
			 * @return Created log entry
			 */
			private LogEntry createLogEntry(Level level, Throwable exception, String message) {
				return new LogEntry(Enabled.class.getName(), TAG, level, exception, message);
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
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceTextMessage() {
				logger.trace("Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an object is discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceMessageObject() {
				logger.trace(42);
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceLazyMessage() {
				logger.trace(() -> "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
			 * is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceFormattedMessageWithArgument() {
				logger.trace("Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
			 * trace severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceFormattedMessageWithLazyArgument() {
				logger.trace("Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceException() {
				logger.trace(new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceExceptionAndTextMessage() {
				logger.trace(new Exception(), "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceExceptionAndLazyMessage() {
				logger.trace(new Exception(), () -> "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
			 * trace severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceExceptionAndFormattedMessageWithArgument() {
				logger.trace(new Exception(), "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.DEBUG)
			@Test
			void traceExceptionAndFormattedMessageWithLazyArgument() {
				logger.trace(new Exception(), "Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a plain text message is discarded if the trace severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugTextMessage() {
				logger.debug("Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugMessageObject() {
				logger.debug(42);
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugLazyMessage() {
				logger.debug(() -> "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
			 * is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugFormattedMessageWithArgument() {
				logger.debug("Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
			 * debug severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugFormattedMessageWithLazyArgument() {
				logger.debug("Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugException() {
				logger.debug(new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugExceptionAndTextMessage() {
				logger.debug(new Exception(), "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugExceptionAndLazyMessage() {
				logger.debug(new Exception(), () -> "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
			 * debug severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugExceptionAndFormattedMessageWithArgument() {
				logger.debug(new Exception(), "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.INFO)
			@Test
			void debugExceptionAndFormattedMessageWithLazyArgument() {
				logger.debug(new Exception(), "Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a plain text message is discarded if the info severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoTextMessage() {
				logger.info("Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoMessageObject() {
				logger.info(42);
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoLazyMessage() {
				logger.info(() -> "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
			 * is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoFormattedMessageWithArgument() {
				logger.info("Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
			 * info severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoFormattedMessageWithLazyArgument() {
				logger.info("Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoException() {
				logger.info(new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message is discarded if the info
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoExceptionAndTextMessage() {
				logger.info(new Exception(), "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoExceptionAndLazyMessage() {
				logger.info(new Exception(), () -> "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
			 * info severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoExceptionAndFormattedMessageWithArgument() {
				logger.info(new Exception(), "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.WARN)
			@Test
			void infoExceptionAndFormattedMessageWithLazyArgument() {
				logger.info(new Exception(), "Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnTextMessage() {
				logger.warn("Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnMessageObject() {
				logger.warn(42);
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnLazyMessage() {
				logger.warn(() -> "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity
			 * level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnFormattedMessageWithArgument() {
				logger.warn("Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
			 * warn severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnFormattedMessageWithLazyArgument() {
				logger.warn("Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnException() {
				logger.warn(new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnExceptionAndTextMessage() {
				logger.warn(new Exception(), "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnExceptionAndLazyMessage() {
				logger.warn(new Exception(), () -> "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
			 * warn severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnExceptionAndFormattedMessageWithArgument() {
				logger.warn(new Exception(), "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.ERROR)
			@Test
			void warnExceptionAndFormattedMessageWithLazyArgument() {
				logger.warn(new Exception(), "Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a plain text message is discarded if the error severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorTextMessage() {
				logger.error("Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorMessageObject() {
				logger.error(42);
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
			 * disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorLazyMessage() {
				logger.error(() -> "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message with placeholders is discarded if the error severity
			 * level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorFormattedMessageWithArgument() {
				logger.error("Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
			 * error severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorFormattedMessageWithLazyArgument() {
				logger.error("Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorException() {
				logger.error(new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorExceptionAndTextMessage() {
				logger.error(new Exception(), "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorExceptionAndLazyMessage() {
				logger.error(new Exception(), () -> "Oops!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
			 * error severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorExceptionAndFormattedMessageWithArgument() {
				logger.error(new Exception(), "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(minLevel = Level.OFF)
			@Test
			void errorExceptionAndFormattedMessageWithLazyArgument() {
				logger.error(new Exception(), "Hello {}!", () -> "Alice");
				assertThat(log.consume()).isEmpty();
			}

		}

	}

}
