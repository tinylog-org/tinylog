package org.tinylog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
	 * Initializes all mocks.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@BeforeAll
	static void init() {
		tinylogMock = mockStatic(Tinylog.class);
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
	 * Restores the tinylog class.
	 */
	@AfterEach
	void dispose() {
		tinylogMock.close();
	}

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

}
