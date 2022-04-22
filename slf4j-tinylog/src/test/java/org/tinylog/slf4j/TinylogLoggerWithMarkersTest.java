package org.tinylog.slf4j;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.core.test.log.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.tinylog.core.test.mockito.MockitoMatchers.isStackTraceElement;

class TinylogLoggerWithMarkersTest {

	private final Marker marker = new BasicMarkerFactory().getDetachedMarker("bar");

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
		 * Verifies the results of the {@link TinylogLogger#isTraceEnabled(Marker)} method.
		 *
		 * @param enabled       The value for {@link LoggingBackend#isEnabled(Object, String, Level)}
		 * @param outputDetails The value for {@link LevelVisibility#getTrace()}
		 */
		@ParameterizedTest
		@CsvSource({
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void isTraceEnabled(boolean enabled, OutputDetails outputDetails) {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			lenient().when(backend.isEnabled(notNull(), eq("bar"), eq(Level.TRACE))).thenReturn(enabled);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			assertThat(logger.isTraceEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
		}

		/**
		 * Verifies the results of the {@link TinylogLogger#isDebugEnabled(Marker)} method.
		 *
		 * @param enabled       The value for {@link LoggingBackend#isEnabled(Object, String, Level)}
		 * @param outputDetails The value for {@link LevelVisibility#getDebug()}
		 */
		@ParameterizedTest
		@CsvSource({
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void isDebugEnabled(boolean enabled, OutputDetails outputDetails) {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			lenient().when(backend.isEnabled(notNull(), eq("bar"), eq(Level.DEBUG))).thenReturn(enabled);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			assertThat(logger.isDebugEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
		}

		/**
		 * Verifies the results of the {@link TinylogLogger#isInfoEnabled(Marker)} method.
		 *
		 * @param enabled       The value for {@link LoggingBackend#isEnabled(Object, String, Level)}
		 * @param outputDetails The value for {@link LevelVisibility#getInfo()}
		 */
		@ParameterizedTest
		@CsvSource({
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void isInfoEnabled(boolean enabled, OutputDetails outputDetails) {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			lenient().when(backend.isEnabled(notNull(), eq("bar"), eq(Level.INFO))).thenReturn(enabled);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			assertThat(logger.isInfoEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
		}

		/**
		 * Verifies the results of the {@link TinylogLogger#isWarnEnabled(Marker)} method.
		 *
		 * @param enabled       The value for {@link LoggingBackend#isEnabled(Object, String, Level)}
		 * @param outputDetails The value for {@link LevelVisibility#getWarn()}
		 */
		@ParameterizedTest
		@CsvSource({
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void isWarnEnabled(boolean enabled, OutputDetails outputDetails) {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED
				)
			);

			lenient().when(backend.isEnabled(notNull(), eq("bar"), eq(Level.WARN))).thenReturn(enabled);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			assertThat(logger.isWarnEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
		}

		/**
		 * Verifies the results of the {@link TinylogLogger#isErrorEnabled(Marker)} method.
		 *
		 * @param enabled       The value for {@link LoggingBackend#isEnabled(Object, String, Level)}
		 * @param outputDetails The value for {@link LevelVisibility#getError()}
		 */
		@ParameterizedTest
		@CsvSource({
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void isErrorEnabled(boolean enabled, OutputDetails outputDetails) {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails
				)
			);

			lenient().when(backend.isEnabled(notNull(), eq("bar"), eq(Level.ERROR))).thenReturn(enabled);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			assertThat(logger.isErrorEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
		}

	}

	/**
	 * Tests for issuing log entries.
	 */
	@Nested
	class LogEntries {

		@Inject
		private Framework framework;

		@Inject
		private Log log;

		private TinylogLogger logger;

		/**
		 * Creates the tagged logger instance and clears all trace and debug log entries, which have been issued while
		 * the creation.
		 */
		@BeforeEach
		void init() {
			logger = new TinylogLogger(TinylogLoggerWithMarkersTest.class.getName(), framework);
			assertThat(log.consume())
				.allSatisfy(entry -> assertThat(entry.getLevel()).isGreaterThanOrEqualTo(Level.DEBUG));
		}


		/**
		 * Tests for issuing log entries if the assigned severity level is enabled.
		 */
		@Nested
		class Enabled {

			/**
			 * Verifies that a trace log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceTextMessage() {
				logger.trace(marker, "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Hello World!", null));
			}

			/**
			 * Verifies that a trace log entry with a plain text message and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceTextMessageAndException() {
				Exception exception = new Exception();
				logger.trace(marker, "Oops!", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Oops!", exception));
			}

			/**
			 * Verifies that a trace log entry with a message and a single placeholder can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceFormattedMessageWithSingleArgument() {
				logger.trace(marker, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Hello Alice!", null));
			}

			/**
			 * Verifies that a trace log entry with a message and two arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceFormattedMessageWithTwoArguments() {
				logger.trace(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Hello Alice and Bob!", null));
			}

			/**
			 * Verifies that a trace log entry with a message, a single placeholder, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceFormattedMessageWithArgumentAndException() {
				Exception exception = new Exception();
				logger.trace(marker, "Oops {}!", "Alice", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Oops Alice!", exception));
			}

			/**
			 * Verifies that a trace log entry with a message and three arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceFormattedMessageWithThreeArguments() {
				logger.trace(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Hello Alice, Bob, and Charlie!", null));
			}

			/**
			 * Verifies that a trace log entry with a message, two placeholders, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void traceFormattedMessageWithArgumentsAndException() {
				Exception exception = new Exception();
				logger.trace(marker, "Oops {} and {}!", "Alice", "Bob", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a full trace log entry can be issued via the generic log method.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			void logTraceInfoEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.TRACE_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a debug log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugTextMessage() {
				logger.debug(marker, "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Hello World!", null));
			}

			/**
			 * Verifies that a debug log entry with a plain text message and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugTextMessageAndException() {
				Exception exception = new Exception();
				logger.debug(marker, "Oops!", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Oops!", exception));
			}

			/**
			 * Verifies that a debug log entry with a message and a single placeholder can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugFormattedMessageWithSingleArgument() {
				logger.debug(marker, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Hello Alice!", null));
			}

			/**
			 * Verifies that a debug log entry with a message and two arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugFormattedMessageWithTwoArguments() {
				logger.debug(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Hello Alice and Bob!", null));
			}

			/**
			 * Verifies that a debug log entry with a message, a single placeholder, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugFormattedMessageWithArgumentAndException() {
				Exception exception = new Exception();
				logger.debug(marker, "Oops {}!", "Alice", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Oops Alice!", exception));
			}

			/**
			 * Verifies that a debug log entry with a message and three arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugFormattedMessageWithThreeArguments() {
				logger.debug(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Hello Alice, Bob, and Charlie!", null));
			}

			/**
			 * Verifies that a debug log entry with a message, two placeholders, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void debugFormattedMessageWithArgumentsAndException() {
				Exception exception = new Exception();
				logger.debug(marker, "Oops {} and {}!", "Alice", "Bob", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a full debug log entry can be issued via the generic log method.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void logDebugInfoEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.DEBUG_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that an info log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoTextMessage() {
				logger.info(marker, "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Hello World!", null));
			}

			/**
			 * Verifies that an info log entry with a plain text message and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoTextMessageAndException() {
				Exception exception = new Exception();
				logger.info(marker, "Oops!", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Oops!", exception));
			}

			/**
			 * Verifies that an info log entry with a message and a single placeholder can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoFormattedMessageWithSingleArgument() {
				logger.info(marker, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Hello Alice!", null));
			}

			/**
			 * Verifies that an info log entry with a message and two arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoFormattedMessageWithTwoArguments() {
				logger.info(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Hello Alice and Bob!", null));
			}

			/**
			 * Verifies that an info log entry with a message, a single placeholder, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoFormattedMessageWithArgumentAndException() {
				Exception exception = new Exception();
				logger.info(marker, "Oops {}!", "Alice", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Oops Alice!", exception));
			}

			/**
			 * Verifies that an info log entry with a message and three arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoFormattedMessageWithThreeArguments() {
				logger.info(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Hello Alice, Bob, and Charlie!", null));
			}

			/**
			 * Verifies that an info log entry with a message, two placeholders, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void infoFormattedMessageWithArgumentsAndException() {
				Exception exception = new Exception();
				logger.info(marker, "Oops {} and {}!", "Alice", "Bob", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a full info log entry can be issued via the generic log method.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void logFullInfoEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.INFO_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a warning log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnTextMessage() {
				logger.warn(marker, "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Hello World!", null));
			}

			/**
			 * Verifies that a warning log entry with a plain text message and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnTextMessageAndException() {
				Exception exception = new Exception();
				logger.warn(marker, "Oops!", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Oops!", exception));
			}

			/**
			 * Verifies that a warning log entry with a message and a single placeholder can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnFormattedMessageWithSingleArgument() {
				logger.warn(marker, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Hello Alice!", null));
			}

			/**
			 * Verifies that a warning log entry with a message and two arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnFormattedMessageWithTwoArguments() {
				logger.warn(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Hello Alice and Bob!", null));
			}

			/**
			 * Verifies that a warning log entry with a message, a single placeholder, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnFormattedMessageWithArgumentAndException() {
				Exception exception = new Exception();
				logger.warn(marker, "Oops {}!", "Alice", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Oops Alice!", exception));
			}

			/**
			 * Verifies that a warning log entry with a message and three arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnFormattedMessageWithThreeArguments() {
				logger.warn(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Hello Alice, Bob, and Charlie!", null));
			}

			/**
			 * Verifies that a warning log entry with a message, two placeholders, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void warnFormattedMessageWithArgumentsAndException() {
				Exception exception = new Exception();
				logger.warn(marker, "Oops {} and {}!", "Alice", "Bob", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a warning error log entry can be issued via the generic log method.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void logFullWarningEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.WARN_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that an error log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorTextMessage() {
				logger.error(marker, "Hello World!");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Hello World!", null));
			}

			/**
			 * Verifies that an error log entry with a plain text message and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorTextMessageAndException() {
				Exception exception = new Exception();
				logger.error(marker, "Oops!", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Oops!", exception));
			}

			/**
			 * Verifies that an error log entry with a message and a single placeholder can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorFormattedMessageWithSingleArgument() {
				logger.error(marker, "Hello {}!", "Alice");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Hello Alice!", null));
			}

			/**
			 * Verifies that an error log entry with a message and two arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorFormattedMessageWithTwoArguments() {
				logger.error(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Hello Alice and Bob!", null));
			}

			/**
			 * Verifies that an error log entry with a message, a single placeholder, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorFormattedMessageWithArgumentAndException() {
				Exception exception = new Exception();
				logger.error(marker, "Oops {}!", "Alice", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Oops Alice!", exception));
			}

			/**
			 * Verifies that an error log entry with a message and three arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorFormattedMessageWithThreeArguments() {
				logger.error(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Hello Alice, Bob, and Charlie!", null));
			}

			/**
			 * Verifies that an error log entry with a message, two placeholders, and an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void errorFormattedMessageWithArgumentsAndException() {
				Exception exception = new Exception();
				logger.error(marker, "Oops {} and {}!", "Alice", "Bob", exception);
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Oops Alice and Bob!", exception));
			}

			/**
			 * Verifies that a full error log entry can be issued via the generic log method.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void logFullErrorEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.ERROR_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, "Oops Alice and Bob!", exception));
			}

			/**
			 * Creates a new log entry.
			 *
			 * @param level     Severity level
			 * @param message   Text message
			 * @param exception Exception or any other kind of throwable
			 * @return Created log entry
			 */
			private LogEntry createLogEntry(Level level, String message, Throwable exception) {
				return new LogEntry(TinylogLoggerWithMarkersTest.class.getName(), "bar", level, exception, message);
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
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceTextMessage() {
				logger.trace(marker, "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a plain text message and an exception is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceTextMessageAndException() {
				logger.trace(marker, "Oops!", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message and a single placeholder is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceFormattedMessageWithSingleArgument() {
				logger.trace(marker, "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message and two arguments is discarded if the trace severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceFormattedMessageWithTwoArguments() {
				logger.trace(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message, a single placeholder, and an exception is discarded if
			 * the trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceFormattedMessageWithArgumentAndException() {
				logger.trace(marker, "Oops {}!", "Alice", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message and three arguments is discarded if the trace severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceFormattedMessageWithThreeArguments() {
				logger.trace(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry with a message, two placeholders, and an exception is discarded if the
			 * trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void traceFormattedMessageWithArgumentsAndException() {
				logger.trace(marker, "Oops {} and {}!", "Alice", "Bob", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a trace log entry issued via the generic log method is discarded if the trace severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			void logFullTraceEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.TRACE_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugTextMessage() {
				logger.debug(marker, "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a plain text message and an exception is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugTextMessageAndException() {
				logger.debug(marker, "Oops!", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message and a single placeholder is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugFormattedMessageWithSingleArgument() {
				logger.debug(marker, "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message and two arguments is discarded if the debug severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugFormattedMessageWithTwoArguments() {
				logger.debug(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message, a single placeholder, and an exception is discarded if
			 * the debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugFormattedMessageWithArgumentAndException() {
				logger.debug(marker, "Oops {}!", "Alice", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message and three arguments is discarded if the debug severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugFormattedMessageWithThreeArguments() {
				logger.debug(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry with a message, two placeholders, and an exception is discarded if the
			 * debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void debugFormattedMessageWithArgumentsAndException() {
				logger.debug(marker, "Oops {} and {}!", "Alice", "Bob", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a debug log entry issued via the generic log method is discarded if the debug severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			void logFullDebugEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.DEBUG_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a plain text message is discarded if the info severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoTextMessage() {
				logger.info(marker, "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a plain text message and an exception is discarded if the info
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoTextMessageAndException() {
				logger.info(marker, "Oops!", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message and a single placeholder is discarded if the info severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoFormattedMessageWithSingleArgument() {
				logger.info(marker, "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message and two arguments is discarded if the info severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoFormattedMessageWithTwoArguments() {
				logger.info(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message, a single placeholder, and an exception is discarded if
			 * the info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoFormattedMessageWithArgumentAndException() {
				logger.info(marker, "Oops {}!", "Alice", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message and three arguments is discarded if the info severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoFormattedMessageWithThreeArguments() {
				logger.info(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry with a message, two placeholders, and an exception is discarded if the
			 * info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void infoFormattedMessageWithArgumentsAndException() {
				logger.info(marker, "Oops {} and {}!", "Alice", "Bob", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an info log entry issued via the generic log method is discarded if the info severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			void logFullInfoEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.INFO_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnTextMessage() {
				logger.warn(marker, "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a plain text message and an exception is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnTextMessageAndException() {
				logger.warn(marker, "Oops!", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message and a single placeholder is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnFormattedMessageWithSingleArgument() {
				logger.warn(marker, "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message and two arguments is discarded if the warn severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnFormattedMessageWithTwoArguments() {
				logger.warn(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message, a single placeholder, and an exception is discarded if
			 * the warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnFormattedMessageWithArgumentAndException() {
				logger.warn(marker, "Oops {}!", "Alice", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message and three arguments is discarded if the warn severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnFormattedMessageWithThreeArguments() {
				logger.warn(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry with a message, two placeholders, and an exception is discarded if the
			 * warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void warnFormattedMessageWithArgumentsAndException() {
				logger.warn(marker, "Oops {} and {}!", "Alice", "Bob", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that a warning log entry issued via the generic log method is discarded if the warn severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			void logFullWarningEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.WARN_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a plain text message is discarded if the error severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorTextMessage() {
				logger.error(marker, "Hello World!");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a plain text message and an exception is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorTextMessageAndException() {
				logger.error(marker, "Oops!", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message and a single placeholder is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorFormattedMessageWithSingleArgument() {
				logger.error(marker, "Hello {}!", "Alice");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message and two arguments is discarded if the error severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorFormattedMessageWithTwoArguments() {
				logger.error(marker, "Hello {} and {}!", "Alice", "Bob");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message, a single placeholder, and an exception is discarded if
			 * the error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorFormattedMessageWithArgumentAndException() {
				logger.error(marker, "Oops {}!", "Alice", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message and three arguments is discarded if the error severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorFormattedMessageWithThreeArguments() {
				logger.error(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry with a message, two placeholders, and an exception is discarded if the
			 * error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void errorFormattedMessageWithArgumentsAndException() {
				logger.error(marker, "Oops {} and {}!", "Alice", "Bob", new Exception());
				assertThat(log.consume()).isEmpty();
			}

			/**
			 * Verifies that an error log entry issued via the generic log method is discarded if the error severity
			 * level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			void logFullErrorEntry() {
				Exception exception = new Exception();

				logger.log(
					marker,
					TinylogLogger.class.getName(),
					LocationAwareLogger.ERROR_INT,
					"Oops {} and {}!",
					new Object[] {"Alice", "Bob"},
					exception
				);

				assertThat(log.consume()).isEmpty();
			}

		}

	}

	/**
	 * Tests for providing the expected stack trace information.
	 */
	@ExtendWith(MockitoExtension.class)
	@Nested
	class StackTraceInformation {

		@Mock
		private LoggingBackend backend;

		private final Framework framework = new Framework(false, false) {

			@Override
			public LoggingBackend getLoggingBackend() {
				return backend;
			}

		};

		/**
		 * Verifies that only the category name is passed as location object, if the severity level is enabled and
		 * requires the caller class name.
		 */
		@Test
		void infoLogWithCategoryOnly() {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			logger.info(marker, "Hello World!");

			verify(backend).log(
				"Foo",
				"bar",
				Level.INFO,
				null,
				"Hello World!",
				null,
				null
			);
		}

		/**
		 * Verifies that a complete stack trace element is passed as location object, if the severity level is enabled
		 * and requires the full location information.
		 */
		@Test
		void infoLogWithFullStackTraceInformation() {
			when(backend.getLevelVisibilityByClass(StackTraceInformation.class.getName())).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			TinylogLogger logger = new TinylogLogger(StackTraceInformation.class.getName(), framework);
			logger.info(marker, "Hello World!");

			verify(backend).log(
				isStackTraceElement(
					StackTraceInformation.class.getName(),
					"infoLogWithFullStackTraceInformation",
					TinylogLoggerWithMarkersTest.class.getSimpleName() + ".java",
					1346
				),
				eq("bar"),
				eq(Level.INFO),
				isNull(),
				eq("Hello World!"),
				isNull(),
				isNull()
			);
		}

		/**
		 * Verifies that only the category name is passed as location object, if the severity level is enabled and
		 * requires the caller class name.
		 */
		@Test
		void genericLogWithCategoryOnly() {
			when(backend.getLevelVisibilityByClass("Foo")).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			TinylogLogger logger = new TinylogLogger("Foo", framework);
			logger.log(
				marker,
				StackTraceInformation.class.getName(),
				LocationAwareLogger.INFO_INT,
				"Hello World!",
				null,
				null
			);

			verify(backend).log(
				"Foo",
				"bar",
				Level.INFO,
				null,
				"Hello World!",
				null,
				null
			);
		}

		/**
		 * Verifies that a complete stack trace element is passed as location object, if the severity level is enabled
		 * and requires the full location information.
		 */
		@Test
		void genericLogWithFullStackTraceInformation() {
			when(backend.getLevelVisibilityByClass(StackTraceInformation.class.getName())).thenReturn(
				new LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			);

			TinylogLogger logger = new TinylogLogger(StackTraceInformation.class.getName(), framework);
			logger.log(
				marker,
				StackTraceInformation.class.getName(),
				LocationAwareLogger.INFO_INT,
				"Hello World!",
				null,
				null
			);

			verify(backend).log(
				isStackTraceElement(
					StackTraceInformation.class.getName(),
					"genericLogWithFullStackTraceInformation",
					TinylogLoggerWithMarkersTest.class.getSimpleName() + ".java",
					1418
				),
				eq("bar"),
				eq(Level.INFO),
				isNull(),
				eq("Hello World!"),
				isNull(),
				isNull()
			);
		}

	}

}
