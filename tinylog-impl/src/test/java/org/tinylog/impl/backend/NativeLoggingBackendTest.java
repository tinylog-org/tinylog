package org.tinylog.impl.backend;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.StackTraceLocation;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.context.ThreadLocalContextStorage;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.tinylog.core.test.InternalAssertions.assertThat;

@CaptureLogEntries
@ExtendWith(MockitoExtension.class)
class NativeLoggingBackendTest {

	@Inject
	private Framework framework;
	
	@Mock
	private Writer writer;

	@Captor
	private ArgumentCaptor<LogEntry> logEntryCaptor;

	/**
	 * Verifies that the correct context storage is used.
	 */
	@Test
	void getContextStorage() {
		NativeLoggingBackend backend = new Builder().rootLevel(Level.OFF).create();
		assertThat(backend.getContextStorage()).isInstanceOf(ThreadLocalContextStorage.class);
	}

	/**
	 * Verifies that the correct level visibility is derived from the enabled severity level.
	 *
	 * @param enabledTag The enabled tag to test (can be {@code null} for being untagged)
	 * @param disabledTag The disabled tag to test (can be {@code null} for being untagged)
	 * @param severityLevel The severity level to test
	 */
	@ParameterizedTest(name = "Enabled Tag: <{0}>, Disabled Tag: <{1}>, Severity Level: <{2}>")
	@MethodSource("getVisibilityArguments")
	void getLevelVisibility(String enabledTag, String disabledTag, Level severityLevel) {
		String writerTag = enabledTag == null ? "-" : enabledTag;

		NativeLoggingBackend backend = new Builder()
			.rootLevel(severityLevel)
			.writers(writerTag, severityLevel, writer)
			.create();

		assertThat(backend.getLevelVisibility(enabledTag)).isEnabledFor(severityLevel);
		assertThat(backend.getLevelVisibility(disabledTag)).isEnabledFor(Level.OFF);
	}

	/**
	 * Verifies that the enabled state of severity levels can be correctly determined without requesting the caller
	 * unnecessary, if there are no custom severity levels.
	 */
	@Test
	void isEnabledWithoutCustomLevels() {
		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.INFO)
			.writers(Level.INFO, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);

		assertThat(backend.isEnabled(location.push(), null, Level.TRACE)).isFalse();
		assertThat(backend.isEnabled(location.push(), null, Level.DEBUG)).isFalse();
		assertThat(backend.isEnabled(location.push(), null, Level.INFO)).isTrue();
		assertThat(backend.isEnabled(location.push(), null, Level.WARN)).isTrue();
		assertThat(backend.isEnabled(location.push(), null, Level.ERROR)).isTrue();

		verify(location, never()).getCallerClassName();
		verify(location, never()).getCallerStackTraceElement();
	}

	/**
	 * Verifies that custom severity levels can be determined for specific classes.
	 *
	 * @param className The class name to test
	 * @param traceEnabled {@code true} if TRACE should be enabled, or {@code false} if TRACE should be disabled
	 * @param debugEnabled {@code true} if DEBUG should be enabled, or {@code false} if DEBUG should be disabled
	 * @param infoEnabled {@code true} if INFO should be enabled, or {@code false} if INFO should be disabled
	 * @param warnEnabled {@code true} if WARN should be enabled, or {@code false} if WARN should be disabled
	 * @param errorEnabled {@code true} if ERROR should be enabled, or {@code false} if ERROR should be disabled
	 */
	@ParameterizedTest
	@CsvSource(value = {
		"org.example.Foo$Bar, false, true , true , true , true",
		"org.example.Foo    , false, true , true , true , true",
		"org.example.Bar    , false, false, false, true , true",
		"org.other.Foo      , false, false, true , true , true"
	})
	void isEnabledWithCustomLevels(
		String className,
		boolean traceEnabled,
		boolean debugEnabled,
		boolean infoEnabled,
		boolean warnEnabled,
		boolean errorEnabled
	) {
		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.INFO)
			.customLevel("org.example", Level.WARN)
			.customLevel("org.example.Foo", Level.DEBUG)
			.writers(Level.DEBUG, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerClassName()).thenReturn(className);

		assertThat(backend.isEnabled(location.push(), null, Level.TRACE)).isEqualTo(traceEnabled);
		assertThat(backend.isEnabled(location.push(), null, Level.DEBUG)).isEqualTo(debugEnabled);
		assertThat(backend.isEnabled(location.push(), null, Level.INFO)).isEqualTo(infoEnabled);
		assertThat(backend.isEnabled(location.push(), null, Level.WARN)).isEqualTo(warnEnabled);
		assertThat(backend.isEnabled(location.push(), null, Level.ERROR)).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that the enabled state can be correctly determined for a real (not mocked) stack trace location.
	 */
	@Test
	void isEnabledForActualStackTrace() {
		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.OFF)
			.customLevel(NativeLoggingBackendTest.class.getName(), Level.INFO)
			.writers(Level.INFO, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		assertThat(backend.isEnabled(location.push(), null, Level.TRACE)).isFalse();
		assertThat(backend.isEnabled(location.push(), null, Level.DEBUG)).isFalse();
		assertThat(backend.isEnabled(location.push(), null, Level.INFO)).isTrue();
		assertThat(backend.isEnabled(location.push(), null, Level.WARN)).isTrue();
		assertThat(backend.isEnabled(location.push(), null, Level.ERROR)).isTrue();
	}

	/**
	 * Verifies that the date and time of issue can be logged.
	 */
	@Test
	void logTimestamp() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.TIMESTAMP));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		Instant minTimestamp = Instant.now();
		backend.log(location.push(), null, Level.INFO, null, null, null, null);
		Instant maxTimestamp = Instant.now();

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getTimestamp()).isBetween(minTimestamp, maxTimestamp);
	}

	/**
	 * Verifies that the passed time since application start can be logged.
	 */
	@Test
	void logUptime() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.UPTIME));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		Duration minUptime = framework.getRuntime().getUptime();
		backend.log(location.push(), null, Level.INFO, null, null, null, null);
		Duration maxUptime = framework.getRuntime().getUptime();

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getUptime()).isBetween(minUptime, maxUptime);
	}

	/**
	 * Verifies that the source thread of issue can be logged.
	 */
	@Test
	void logThread() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.THREAD));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getThread()).isSameAs(Thread.currentThread());
	}

	/**
	 * Verifies that the present thread context values can be logged.
	 */
	@Test
	void logContext() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.CONTEXT));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		backend.getContextStorage().put("foo", "bar");
		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, null, null, null);
		backend.getContextStorage().clear();

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getContext()).containsExactly(entry("foo", "bar"));
	}

	/**
	 * Verifies that the source class name can be logged.
	 */
	@Test
	void logClass() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.CLASS));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerClassName()).thenReturn("Foo");
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getClassName()).isEqualTo("Foo");
	}

	/**
	 * Verifies that the source method name can be logged.
	 */
	@Test
	void logMethod() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.METHOD));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerStackTraceElement())
			.thenReturn(new StackTraceElement("Foo", "bar", "Foo.java", 42));
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMethodName()).isEqualTo("bar");
	}

	/**
	 * Verifies that the source file name can be logged.
	 */
	@Test
	void logFile() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.FILE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerStackTraceElement())
			.thenReturn(new StackTraceElement("Foo", "bar", "Foo.java", 42));
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getFileName()).isEqualTo("Foo.java");
	}

	/**
	 * Verifies that the line number can be logged.
	 */
	@Test
	void logLine() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.LINE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerStackTraceElement())
			.thenReturn(new StackTraceElement("Bar", "foo", "Bar.java", 42));
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getLineNumber()).isEqualTo(42);
	}

	/**
	 * Verifies that the assigned tag can be logged.
	 */
	@Test
	void logTag() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.TAG));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), "foo", Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getTag()).isEqualTo("foo");
	}

	/**
	 * Verifies that the severity level can be logged.
	 */
	@Test
	void logLevel() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.LEVEL));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getSeverityLevel()).isEqualTo(Level.INFO);
	}

	/**
	 * Verifies that a plain text message without arguments and formatter can be logged.
	 */
	@Test
	void logPlainTextMessage() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, "Hello World!", null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a formatted message with arguments can be resolved and logged.
	 */
	@Test
	void logFormattedMessage() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(
			location.push(),
			null,
			Level.INFO,
			null,
			"Hello {}!",
			new Object[] {"Alice"},
			new EnhancedMessageFormatter(framework)
		);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello Alice!");
	}

	/**
	 * Verifies that any kind of object can be logged as message.
	 */
	@Test
	void logObjectMessage() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, 42, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("42");
	}

	/**
	 * Verifies that an exception can be logged.
	 */
	@Test
	void logException() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.EXCEPTION));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		Exception exception = new Exception();
		backend.log(location.push(), null, Level.INFO, exception, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getException()).isSameAs(exception);
	}

	/**
	 * Verifies that log entries will be only output, if the severity level is enabled for the caller class.
	 *
	 * @param className The class name to test
	 * @param disabledLevel A disabled severity level to test
	 * @param enabledLevel An enabled severity level to test
	 */
	@ParameterizedTest
	@CsvSource(value = {
		"org.example.Foo$Bar, TRACE, DEBUG",
		"org.example.Foo    , TRACE, DEBUG",
		"org.example.Bar    , INFO , WARN ",
		"org.other.Foo      , DEBUG, INFO "
	})
	void logWithCustomLevels(String className, Level disabledLevel, Level enabledLevel) throws Exception {
		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.INFO)
			.customLevel("org.example", Level.WARN)
			.customLevel("org.example.Foo", Level.DEBUG)
			.writers(Level.DEBUG, writer)
			.create();

		StackTraceLocation location = mock(StackTraceLocation.class);
		when(location.push()).thenReturn(location);
		when(location.getCallerClassName()).thenReturn(className);

		backend.log(location.push(), null, disabledLevel, null, null, null, null);
		backend.log(location.push(), null, enabledLevel, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getSeverityLevel()).isEqualTo(enabledLevel);
	}

	/**
	 * Verifies that the real (not mocked) caller class name is used for determining whether a log entry will be output.
	 */
	@Test
	void logForActualStackTrace() throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.LEVEL));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.OFF)
			.customLevel(NativeLoggingBackendTest.class.getName(), Level.INFO)
			.writers(Level.INFO, writer)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.DEBUG, null, null, null, null);
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getSeverityLevel()).isEqualTo(Level.INFO);
	}

	/**
	 * Verifies that an async writer can be used for asynchronous logging.
	 */
	@Test
	void logWithAsyncWriter() throws Exception {
		AsyncWriter asyncWriter = mock(AsyncWriter.class);

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.TRACE)
			.writers(Level.TRACE, asyncWriter)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, null, null, null);

		Thread.currentThread().join(100);

		verify(asyncWriter).log(any());
		verify(asyncWriter).flush();
	}

	/**
	 * Verifies that failed output of an untagged log entry is reported to the {@link InternalLogger}.
	 *
	 * @param log The internal log with logged warnings and errors
	 */
	@Test
	void logUntaggedLogEntryToFailedWriter(Log log) throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));
		doThrow(IOException.class).when(writer).log(any());

		Writer otherWriter = mock(Writer.class);
		when(otherWriter.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.INFO)
			.writers(Level.INFO, writer, otherWriter)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), null, Level.INFO, null, "Hello World!", null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello World!");

		verify(otherWriter).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello World!");

		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).isInstanceOf(IOException.class);
		});
	}

	/**
	 * Verifies that failed output of an internal tinylog log entry is ignored.
	 *
	 * @param log The internal log with logged warnings and errors
	 */
	@Test
	void logInternalTinylogLogEntryToFailedWriter(Log log) throws Exception {
		when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));
		doThrow(IOException.class).when(writer).log(any());

		Writer otherWriter = mock(Writer.class);
		when(otherWriter.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

		NativeLoggingBackend backend = new Builder()
			.rootLevel(Level.INFO)
			.writers(Level.INFO, writer, otherWriter)
			.create();

		StackTraceLocation location = framework.getRuntime().getStackTraceLocationAtIndex(0);
		backend.log(location.push(), "tinylog", Level.WARN, null, "Hello tinylog!", null, null);

		verify(writer).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello tinylog!");

		verify(otherWriter).log(logEntryCaptor.capture());
		assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello tinylog!");

		assertThat(log.consume()).isEmpty();
	}

	/**
	 * Creates the arguments for the parameterized test {@link #getLevelVisibility(String, String, Level)}.
	 *
	 * <p>
	 *     Arguments:
	 *     <ul>
	 *         <li>The enabled tag to test</li>
	 *         <li>The disabled tag to test</li>
	 *         <li>The severity level to test</li>
	 *     </ul>
	 * </p>
	 *
	 * @return All arguments to test
	 */
	private static Collection<Arguments> getVisibilityArguments() {
		Collection<Arguments> arguments = new ArrayList<>();
		for (Level level : Level.values()) {
			if (level != Level.OFF) {
				arguments.add(Arguments.of(null, "foo", level));
				arguments.add(Arguments.of("foo", null, level));
			}
		}
		return arguments;
	}

	/**
	 * Builder with a fluent API for configuring and creating instances of {@link NativeLoggingBackend}.
	 */
	private final class Builder {

		private final Map<String, LevelConfiguration> levelConfigurations;
		private final Map<String, Multimap<Level, Writer>> writers;

		/** */
		private Builder() {
			levelConfigurations = new HashMap<>();
			writers = new HashMap<>();
			writers.put(LevelConfiguration.UNTAGGED_PLACEHOLDER, HashMultimap.create());
			writers.put(LevelConfiguration.TAGGED_PLACEHOLDER, HashMultimap.create());
		}

		/**
		 * Sets the general severity level for all classes and packages without custom severity levels.
		 *
		 * @param level The general severity level
		 * @return The same instance of this builder
		 */
		public Builder rootLevel(Level level) {
			return customLevel("", level);
		}

		/**
		 * Sets a custom severity level for a package or class.
		 *
		 * @param packageOrClass The fully-qualified name of the package or class to configure
		 * @param level The custom severity level for the passed package or class
		 * @return The same instance of this builder
		 */
		public Builder customLevel(String packageOrClass, Level level) {
			Map<String, Level> mappedLevels = ImmutableMap.of(
				LevelConfiguration.UNTAGGED_PLACEHOLDER, level,
				LevelConfiguration.TAGGED_PLACEHOLDER, level
			);

			levelConfigurations.put(packageOrClass, new LevelConfiguration(mappedLevels));

			return this;
		}

		/**
		 * Registers writers for a certain severity level.
		 *
		 * @param level The passed writers should receive only log entries with this severity level or more severe
		 *              levels
		 * @param writers The writers to register
		 * @return The same instance of this builder
		 */
		public Builder writers(Level level, Writer... writers) {
			writers(LevelConfiguration.UNTAGGED_PLACEHOLDER, level, writers);
			writers(LevelConfiguration.TAGGED_PLACEHOLDER, level, writers);
			return this;
		}

		/**
		 * Registers writers for a certain tag and severity level.
		 *
		 * @param tag The passed writers should only receive log entries with this tag
		 * @param level The passed writers should only receive log entries with this severity level or more severe
		 *              levels
		 * @param writers The writers to register
		 * @return The same instance of this builder
		 */
		public Builder writers(String tag, Level level, Writer... writers) {
			for (Level existingLevel : Level.values()) {
				if (existingLevel.isAtLeastAsSevereAs(level) && existingLevel != Level.OFF) {
					for (Writer writer : writers) {
						this.writers
							.computeIfAbsent(tag, key -> HashMultimap.create())
							.put(existingLevel, writer);
					}
				}
			}
			return this;
		}

		/**
		 * Creates a new {@link NativeLoggingBackend} based on the provided configuration.
		 *
		 * @return A new instance of {@link NativeLoggingBackend}
		 */
		public NativeLoggingBackend create() {
			Map<String, Map<Level, WriterRepository>> repositoriesForTags = new HashMap<>();
			for (String tag : writers.keySet()) {
				Map<Level, WriterRepository> repositoriesForLevels = new EnumMap<>(Level.class);
				for (Level level : Level.values()) {
					if (level != Level.OFF) {
						Collection<Writer> collection = writers.getOrDefault(tag, ImmutableMultimap.of()).get(level);
						repositoriesForLevels.put(level, new WriterRepository(collection));
					}
				}
				repositoriesForTags.put(tag, repositoriesForLevels);
			}

			LoggingConfiguration loggingConfiguration = new LoggingConfiguration(
				levelConfigurations,
				repositoriesForTags
			);

			Collection<AsyncWriter> asyncWriters = writers.values().stream()
				.flatMap(map -> map.values().stream())
				.filter(writer -> writer instanceof AsyncWriter)
				.map(writer -> (AsyncWriter) writer)
				.collect(Collectors.toSet());

			WritingThread writingThread = asyncWriters.isEmpty() ? null : new WritingThread(asyncWriters, 64);
			return new NativeLoggingBackend(framework, loggingConfiguration, writingThread);
		}

	}

}
