package org.tinylog;

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
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LoggerTest {

	private static final LoggingBackend backend = mock(LoggingBackend.class);
	private static final LevelVisibility visibility = mock(LevelVisibility.class);

	private static final Framework framework = new Framework(false, false) {

		@Override
		public LoggingBackend getLoggingBackend() {
			return backend;
		}

	};

	private static MockedStatic<Tinylog> tinylogMock;

	/**
	 * Mocks the static tinylog class.
	 */
	@BeforeAll
	static void create() {
		tinylogMock = mockStatic(Tinylog.class);
	}

	/**
	 * Restores the mocked tinylog class.
	 */
	@AfterAll
	static void dispose() {
		tinylogMock.close();
	}

	/**
	 * Initializes all mocks.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@BeforeEach
	void init() {
		tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
		when(backend.getLevelVisibility(null)).thenReturn(visibility);
	}

	/**
	 * Resets all mocks.
	 */
	@AfterEach
	void reset() {
		tinylogMock.reset();
		Mockito.reset(backend, visibility);
	}

	/**
	 * Tests for category tests.
	 */
	@Nested
	public class Tags {

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
	public class Levels {

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
			lenient().when(backend.isEnabled(notNull(), eq(null), eq(Level.TRACE))).thenReturn(enabled);

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
			lenient().when(backend.isEnabled(notNull(), eq(null), eq(Level.DEBUG))).thenReturn(enabled);

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
			lenient().when(backend.isEnabled(notNull(), eq(null), eq(Level.INFO))).thenReturn(enabled);

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
			lenient().when(backend.isEnabled(notNull(), eq(null), eq(Level.WARN))).thenReturn(enabled);

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
			lenient().when(backend.isEnabled(notNull(), eq(null), eq(Level.ERROR))).thenReturn(enabled);

			assertThat(Logger.isErrorEnabled()).isEqualTo(visible && enabled);
		}

	}

}
