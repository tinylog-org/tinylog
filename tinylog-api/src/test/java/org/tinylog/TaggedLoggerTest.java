package org.tinylog;

import javax.inject.Inject;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class TaggedLoggerTest {

	/**
	 * Tests for category tags.
	 */
	@CaptureLogEntries
	@Nested
	public class Tags {

		@Inject
		private Framework framework;

		/**
		 * Verifies that a string can be assigned as tag.
		 */
		@Test
		void stringTag() {
			TaggedLogger logger = new TaggedLogger("dummy", framework);
			assertThat(logger.getTag()).isEqualTo("dummy");
		}

		/**
		 * Verifies that {@code null} can be passed as tag for creating an untagged logger.
		 */
		@Test
		void nullTag() {
			TaggedLogger logger = new TaggedLogger(null, framework);
			assertThat(logger.getTag()).isNull();
		}

	}

	/**
	 * Tests for severity levels.
	 */
	@ExtendWith(MockitoExtension.class)
	@Nested
	public class Levels {

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
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isTraceEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(visible, false, false, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.TRACE))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger("test", framework);
			assertThat(logger.isTraceEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isDebugEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isDebugEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isDebugEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, visible, false, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.DEBUG))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger("test", framework);
			assertThat(logger.isDebugEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isInfoEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isInfoEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isInfoEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, visible, false, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.INFO))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger("test", framework);
			assertThat(logger.isInfoEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isWarnEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isWarnEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isWarnEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, true, visible, false)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.WARN))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger("test", framework);
			assertThat(logger.isWarnEnabled()).isEqualTo(visible && enabled);
		}

		/**
		 * Verifies the results of the {@link TaggedLogger#isErrorEnabled()} method.
		 *
		 * @param visible The value for {@link LevelVisibility#isErrorEnabled()}
		 * @param enabled The value for {@link LoggingBackend#isEnabled(StackTraceLocation, String, Level)}
		 */
		@ParameterizedTest
		@CsvSource(value = {"false,false", "false,true", "true,false", "true,true"})
		void isErrorEnabled(boolean visible, boolean enabled) {
			when(backend.getLevelVisibility("test")).thenReturn(
				new LevelVisibility(true, true, true, true, visible)
			);

			lenient().when(backend.isEnabled(notNull(), eq("test"), eq(Level.ERROR))).thenReturn(enabled);

			TaggedLogger logger = new TaggedLogger("test", framework);
			assertThat(logger.isErrorEnabled()).isEqualTo(visible && enabled);
		}

	}

}
