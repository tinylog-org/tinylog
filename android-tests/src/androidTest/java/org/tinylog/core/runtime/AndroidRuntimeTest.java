package org.tinylog.core.runtime;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tinylog.test.assertj.LocationInfoAssert.assertThatLocationInfo;

@Tinylog
class AndroidRuntimeTest {

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    private RuntimeFlavor runtime;

    /**
     * Creates an instance of {@link LegacyAndroidRuntime} or {@link ModernAndroidRuntime}.
     */
    @BeforeEach
    void init() {
        runtime = new AndroidRuntimeBuilder().create(logger);
    }

    /**
     * Verifies that a name and version is provided as operating system.
     */
    @Test
    void operatingSystem() {
        assertThat(runtime.getOperatingSystem()).matches(".* \\d+.*");
    }

    /**
     * Verifies that a name and version is provided as virtual machine.
     */
    @Test
    void virtualMachine() {
        assertThat(runtime.getVirtualMachine()).matches(".* \\d+.*");
    }

    /**
     * Verifies that a valid process ID is provided.
     */
    @Test
    void processId() {
        assertThat(runtime.getProcessId()).isGreaterThan(0);
    }

    /**
     * Verifies that valid uptime values are provided.
     */
    @Test
    void uptime() throws InterruptedException {
        Duration time1 = runtime.getUptime();
        assertThat(time1).isBetween(Duration.ZERO, Duration.ofHours(1));

        Thread.sleep(100);

        Duration time2 = runtime.getUptime();
        assertThat(time2).isGreaterThan(time1);
    }

    /**
     * Verifies that {@code logcat} is the default writer.
     */
    @Test
    void defaultWriter() {
        assertThat(runtime.getDefaultWriter()).isEqualTo("logcat");
    }

    /**
     * Tests for {@link RuntimeFlavor#getDirectCaller(OutputDetails)}.
     */
    @Nested
    class DirectCaller {

        /**
         * Verifies that the expected {@link StackTraceElement} is returned for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFO}.
         */
        @Test
        void getFullLocationInformation() {
            Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            Object result = Callee.execute(supplier);

            assertThatLocationInfo(result).isStackTraceElement(
                DirectCaller.class,
                "getFullLocationInformation",
                AndroidRuntimeTest.class.getSimpleName() + ".java",
                105
            );
        }

        /**
         * Verifies that the expected caller class is returned for {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}.
         */
        @Test
        void getCallerClass() {
            Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(supplier);

            assertThatLocationInfo(result).hasClass(DirectCaller.class);
        }

        /**
         * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
         * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFO}.
         *
         * @param outputDetails {@link OutputDetails#DISABLED} or {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFO}
         */
        @ParameterizedTest
        @EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFO"})
        void getDisabledOrWithoutLocationInformation(OutputDetails outputDetails) {
            Supplier<?> supplier = runtime.getDirectCaller(outputDetails);
            Object result = Callee.execute(supplier);

            assertThatLocationInfo(result).isNull();
        }

    }

    /**
     * Tests for {@link RuntimeFlavor#getRelativeCaller(OutputDetails)}.
     */
    @Nested
    class RelativeCaller {

        /**
         * Verifies that the expected {@link StackTraceElement} is returned for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFO} if a class name
         * is passed that actually exists in the stack trace.
         */
        @Test
        void getValidFullLocationInformation() {
            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            Object result = Callee.execute(function, Callee.class.getName());

            assertThatLocationInfo(result).isStackTraceElement(
                RelativeCaller.class,
                "getValidFullLocationInformation",
                AndroidRuntimeTest.class.getSimpleName() + ".java",
                157
            );
        }

        /**
         * Verifies that {@code null} is returned and a warning log entry is logged for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFO} if a class name is passed
         * that does not exist in the stack trace.
         */
        @Test
        void getInvalidFullLocationInformation() {
            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            Object result = Callee.execute(function, "org.tinylog.invalid.Foo");

            assertThatLocationInfo(result).isNull();
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
                assertThat(entry.getFormattedMessage(configuration)).contains("org.tinylog.invalid.Foo");
            });
        }

        /**
         * Verifies that the expected caller class is returned for {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}
         * if a class name is passed that actually exists in the stack trace.
         */
        @Test
        void getValidCallerClassName() {
            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(function, Callee.class.getName());

            assertThatLocationInfo(result).hasClass(RelativeCaller.class);
        }

        /**
         * Verifies that {@code null} is returned and a warning log entry is logged for
         * {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} if a class name is passed
         * that does not exist in the stack trace.
         */
        @Test
        void getInvalidCallerClassName() {
            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(function, "org.tinylog.invalid.Foo");

            assertThatLocationInfo(result).isNull();
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
                assertThat(entry.getFormattedMessage(configuration)).contains("org.tinylog.invalid.Foo");
            });
        }

        /**
         * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
         * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFO}.
         *
         * @param outputDetails {@link OutputDetails#DISABLED} or {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFO}
         */
        @ParameterizedTest
        @EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFO"})
        void getDisabledOrWithoutLocationInformation(OutputDetails outputDetails) {
            Function<String, ?> function = runtime.getRelativeCaller(outputDetails);
            Object result = Callee.execute(function, Callee.class.getName());

            assertThatLocationInfo(result).isNull();
        }

    }

    /**
     * Helper class for simulating a callee.
     */
    private static final class Callee {

        /**
         * Executes the passed {@link Supplier}.
         *
         * @param supplier The supplier to execute
         * @param <T> Return type
         * @return The produced value from the passed supplier
         */
        static <T> T execute(Supplier<T> supplier) {
            return supplier.get();
        }

        /**
         * Executes the passed {@link Function}.
         *
         * @param function The function to execute
         * @param argument The argument for the passed function
         * @param <T> Argument type
         * @param <R> Return type
         * @return The produced value from the passed function
         */
        static <T, R> R execute(Function<T, R> function, T argument) {
            return function.apply(argument);
        }

    }

}
