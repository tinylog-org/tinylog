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
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.internal.InternalLogger;
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

import static java.util.Arrays.asList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.noneOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
     * Verifies that the expected {@link OutputDetails} are provided by the {@link LevelVisibility} for given
     * fully-qualified class name.
     *
     * @param className The fully-qualified class name to test
     * @param requiredLogEntryValues The required log entry values to test
     * @param outputDetails The expected output details to assert
     */
    @ParameterizedTest
    @MethodSource("getVisibilityClassArguments")
    void getLevelVisibilityForEnabledClasses(
        String className,
        Set<LogEntryValue> requiredLogEntryValues,
        OutputDetails outputDetails
    ) {
        when(writer.getRequiredLogEntryValues()).thenReturn(requiredLogEntryValues);

        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.OFF)
            .customLevel(className, Level.TRACE)
            .writers("foo", Level.INFO, writer)
            .create();

        LevelVisibility visibility = backend.getLevelVisibilityByClass(className);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(outputDetails);
        assertThat(visibility.getWarn()).isEqualTo(outputDetails);
        assertThat(visibility.getError()).isEqualTo(outputDetails);
    }

    /**
     * Verifies that the {@link LevelVisibility} for a disabled class will return {@link OutputDetails#DISABLED} for
     * every severity level.
     *
     * @param className The fully-qualified class name to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.InternalLoggingBackend"})
    void getLevelVisibilityForDisabledClasses(String className) {
        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.TRACE)
            .customLevel(className, Level.OFF)
            .writers(Level.TRACE, writer)
            .create();

        LevelVisibility visibility = backend.getLevelVisibilityByClass(className);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that the correct {@link LevelVisibility} is provided for the root level and a custom package level
     * respectively.
     */
    @Test
    void getDifferentLevelVisibilityForDifferentClasses() {
        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.INFO)
            .customLevel("example", Level.DEBUG)
            .writers(Level.TRACE, writer)
            .create();

        LevelVisibility visibility = backend.getLevelVisibilityByClass("Main");
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);

        visibility = backend.getLevelVisibilityByClass("example.Foo");
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION);
    }

    /**
     * Verifies that the expected {@link OutputDetails} are provided by the {@link LevelVisibility} for given required
     * log entry values at a used tag.
     *
     * @param tag The enabled tag to test
     * @param requiredLogEntryValues The required log entry values to test
     * @param outputDetails The expected output details to assert
     */
    @ParameterizedTest
    @MethodSource("getVisibilityTagArguments")
    void getLevelVisibilityForEnabledTags(
        String tag,
        Set<LogEntryValue> requiredLogEntryValues,
        OutputDetails outputDetails
    ) {
        when(writer.getRequiredLogEntryValues()).thenReturn(requiredLogEntryValues);

        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.INFO)
            .writers(tag, Level.INFO, writer)
            .create();

        LevelVisibility visibility = backend.getLevelVisibilityByTag(tag);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(outputDetails);
        assertThat(visibility.getWarn()).isEqualTo(outputDetails);
        assertThat(visibility.getError()).isEqualTo(outputDetails);
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

        assertThat(backend.isEnabled(null, null, Level.TRACE)).isFalse();
        assertThat(backend.isEnabled(null, null, Level.DEBUG)).isFalse();
        assertThat(backend.isEnabled(null, null, Level.INFO)).isTrue();
        assertThat(backend.isEnabled(null, null, Level.WARN)).isTrue();
        assertThat(backend.isEnabled(null, null, Level.ERROR)).isTrue();
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

        assertThat(backend.isEnabled(className, null, Level.TRACE)).isEqualTo(traceEnabled);
        assertThat(backend.isEnabled(className, null, Level.DEBUG)).isEqualTo(debugEnabled);
        assertThat(backend.isEnabled(className, null, Level.INFO)).isEqualTo(infoEnabled);
        assertThat(backend.isEnabled(className, null, Level.WARN)).isEqualTo(warnEnabled);
        assertThat(backend.isEnabled(className, null, Level.ERROR)).isEqualTo(errorEnabled);
    }

    /**
     * Verifies that the enabled state can be correctly determined for a {@link Class}.
     */
    @Test
    void isEnabledForClass() {
        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.OFF)
            .customLevel(NativeLoggingBackendTest.class.getName(), Level.INFO)
            .writers(Level.INFO, writer)
            .create();

        assertThat(backend.isEnabled(NativeLoggingBackendTest.class, null, Level.TRACE)).isFalse();
        assertThat(backend.isEnabled(NativeLoggingBackendTest.class, null, Level.DEBUG)).isFalse();
        assertThat(backend.isEnabled(NativeLoggingBackendTest.class, null, Level.INFO)).isTrue();
        assertThat(backend.isEnabled(NativeLoggingBackendTest.class, null, Level.WARN)).isTrue();
        assertThat(backend.isEnabled(NativeLoggingBackendTest.class, null, Level.ERROR)).isTrue();
    }

    /**
     * Verifies that the enabled state can be correctly determined for a {@link StackTraceElement}.
     */
    @Test
    void isEnabledForStackTraceElement() {
        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.OFF)
            .customLevel(NativeLoggingBackendTest.class.getName(), Level.INFO)
            .writers(Level.INFO, writer)
            .create();

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        assertThat(backend.isEnabled(stackTraceElement, null, Level.TRACE)).isFalse();
        assertThat(backend.isEnabled(stackTraceElement, null, Level.DEBUG)).isFalse();
        assertThat(backend.isEnabled(stackTraceElement, null, Level.INFO)).isTrue();
        assertThat(backend.isEnabled(stackTraceElement, null, Level.WARN)).isTrue();
        assertThat(backend.isEnabled(stackTraceElement, null, Level.ERROR)).isTrue();
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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        Instant minTimestamp = Instant.now();
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);
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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        Duration minUptime = framework.getRuntime().getUptime();
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);
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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);
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

        backend.log("Foo", null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new StackTraceElement("Foo", "bar", "Foo.java", 42);
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new StackTraceElement("Foo", "bar", "Foo.java", 42);
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new StackTraceElement("Foo", "bar", "Foo.java", 42);
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, "foo", Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, "Hello World!", null, null);

        verify(writer).log(logEntryCaptor.capture());
        assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello World!");
    }

    /**
     * Verifies that a formatted message with arguments can be resolved and logged.
     */
    @Test
    void logFormattedMessageWithArguments() throws Exception {
        when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.TRACE)
            .writers(Level.TRACE, writer)
            .create();

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(
            stackTraceElement,
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
     * Verifies that a formatted message without arguments can be logged.
     */
    @Test
    void logFormattedMessageWithoutArguments() throws Exception {
        when(writer.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.MESSAGE));

        NativeLoggingBackend backend = new Builder()
            .rootLevel(Level.TRACE)
            .writers(Level.TRACE, writer)
            .create();

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(
            stackTraceElement,
            null,
            Level.INFO,
            null,
            "Hello {}!",
            null,
            new EnhancedMessageFormatter(framework)
        );

        verify(writer).log(logEntryCaptor.capture());
        assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello {}!");
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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, 42, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        Exception exception = new Exception();
        backend.log(stackTraceElement, null, Level.INFO, exception, null, null, null);

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

        backend.log(className, null, disabledLevel, null, null, null, null);
        backend.log(className, null, enabledLevel, null, null, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.DEBUG, null, null, null, null);
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, null, null, null);

        Thread.sleep(1000);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, null, Level.INFO, null, "Hello World!", null, null);

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

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        backend.log(stackTraceElement, "tinylog", Level.WARN, null, "Hello tinylog!", null, null);

        verify(writer).log(logEntryCaptor.capture());
        assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello tinylog!");

        verify(otherWriter).log(logEntryCaptor.capture());
        assertThat(logEntryCaptor.getValue().getMessage()).isEqualTo("Hello tinylog!");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Creates the arguments for the parameterized test
     * {@link #getLevelVisibilityForDisabledClasses(String)} (String, Set, OutputDetails)}.
     *
     * <p>
     *     Arguments:
     *     <ul>
     *         <li>The fully-qualified class name to test</li>
     *         <li>The required log entry values to test</li>
     *         <li>The expected output details to assert</li>
     *     </ul>
     * </p>
     *
     * @return All arguments to test
     */
    private static Collection<Arguments> getVisibilityClassArguments() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (String className : asList("Foo", "example.Foo", "org.tinylog.impl.backend.NativeLoggingBackend")) {
            arguments.add(Arguments.of(
                className,
                noneOf(LogEntryValue.class),
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                className,
                complementOf(
                    EnumSet.of(LogEntryValue.CLASS, LogEntryValue.FILE, LogEntryValue.METHOD, LogEntryValue.LINE)
                ),
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                className,
                EnumSet.of(LogEntryValue.CLASS),
                OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)
            );
            arguments.add(Arguments.of(
                className,
                EnumSet.of(LogEntryValue.FILE),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                className,
                EnumSet.of(LogEntryValue.METHOD),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                className,
                EnumSet.of(LogEntryValue.LINE),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
        }
        return arguments;
    }

    /**
     * Creates the arguments for the parameterized test
     * {@link #getLevelVisibilityForEnabledTags(String, Set, OutputDetails)}.
     *
     * <p>
     *     Arguments:
     *     <ul>
     *         <li>The enabled tag to test</li>
     *         <li>The required log entry values to test</li>
     *         <li>The expected output details to assert</li>
     *     </ul>
     * </p>
     *
     * @return All arguments to test
     */
    private static Collection<Arguments> getVisibilityTagArguments() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (String tag : asList("-", "foo", "tinylog")) {
            arguments.add(Arguments.of(
                tag,
                noneOf(LogEntryValue.class),
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                tag,
                complementOf(
                    EnumSet.of(LogEntryValue.CLASS, LogEntryValue.FILE, LogEntryValue.METHOD, LogEntryValue.LINE)
                ),
                OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                tag,
                EnumSet.of(LogEntryValue.CLASS),
                OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)
            );
            arguments.add(Arguments.of(
                tag,
                EnumSet.of(LogEntryValue.FILE),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                tag,
                EnumSet.of(LogEntryValue.METHOD),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
            arguments.add(Arguments.of(
                tag,
                EnumSet.of(LogEntryValue.LINE),
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION)
            );
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
