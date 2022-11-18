package org.tinylog.core.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@CaptureLogEntries
@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
class JavaRuntimeTest {

    @Inject
    private Log log;

    /**
     * Verifies that valid uptime values are provided.
     */
    @Test
    void uptime() throws InterruptedException {
        JavaRuntime runtime = new JavaRuntime();

        Duration time1 = runtime.getUptime();
        assertThat(time1).isBetween(Duration.ZERO, Duration.ofHours(1));

        Thread.sleep(10);

        Duration time2 = runtime.getUptime();
        assertThat(time2).isGreaterThan(time1);
    }

    /**
     * Verifies that {@code console} the default writer.
     */
    @Test
    void defaultWriter() {
        JavaRuntime runtime = new JavaRuntime();
        assertThat(runtime.getDefaultWriter()).isEqualTo("console");
    }

    @Nested
    class ProcessId {

        /**
         * Verifies that a valid process ID is provided.
         */
        @Test
        void validProcessId() {
            long pid = new JavaRuntime().getProcessId();
            assertThat(pid).isGreaterThan(0);
        }

        /**
         * Verifies that a meaningful error will be logged, if the process ID cannot be resolved from
         * {@link RuntimeMXBean#getName()}.
         *
         * @param runtimeName The invalid runtime name to test
         */
        @ParameterizedTest
        @ValueSource(strings = {"bar", "foo@localhost"})
        void invalidProcessId(String runtimeName) {
            RuntimeMXBean bean = mock(RuntimeMXBean.class);
            when(bean.getName()).thenReturn(runtimeName);

            try (MockedStatic<ManagementFactory> factory = mockStatic(ManagementFactory.class)) {
                factory.when(ManagementFactory::getRuntimeMXBean).thenReturn(bean);

                long pid = new JavaRuntime().getProcessId();
                assertThat(pid).isEqualTo(-1);
                assertThat(log.consume()).singleElement().satisfies(entry -> {
                    assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
                    assertThat(entry.getMessage()).contains(runtimeName);
                });
            }
        }

    }

    /**
     * Tests for {@link JavaRuntime#getDirectCaller(OutputDetails)}.
     */
    @Nested
    @CaptureLogEntries
    class DirectCaller {

        /**
         * Verifies that the expected {@link StackTraceElement} is returned for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}.
         */
        @Test
        void getFullLocationInformation() {
            JavaRuntime runtime = new JavaRuntime();

            Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION);
            Object result = Callee.execute(supplier);

            assertThat(result).isInstanceOfSatisfying(StackTraceElement.class, element -> {
                assertThat(element.getClassName()).isEqualTo(DirectCaller.class.getName());
                assertThat(element.getMethodName()).isEqualTo("getFullLocationInformation");
                assertThat(element.getFileName()).isEqualTo(JavaRuntimeTest.class.getSimpleName() + ".java");
                assertThat(element.getLineNumber()).isEqualTo(114);
            });
        }

        /**
         * Verifies that the expected caller class is returned for
         * {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}.
         */
        @Test
        void getCallerClass() {
            JavaRuntime runtime = new JavaRuntime();

            Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(supplier);

            assertThat(result).isEqualTo(DirectCaller.class);
        }

        /**
         * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
         * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}.
         *
         * @param outputDetails {@link OutputDetails#DISABLED} or
         *                      {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}
         */
        @ParameterizedTest
        @EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFORMATION"})
        void getDisabledOrWithoutLocationInformation(OutputDetails outputDetails) {
            JavaRuntime runtime = new JavaRuntime();

            Supplier<?> supplier = runtime.getDirectCaller(outputDetails);
            Object result = Callee.execute(supplier);

            assertThat(result).isNull();
        }

    }

    /**
     * Tests for {@link JavaRuntime#getRelativeCaller(OutputDetails)}.
     */
    @Nested
    @CaptureLogEntries
    class RelativeCaller {

        @Inject
        private Log log;

        /**
         * Verifies that the expected {@link StackTraceElement} is returned for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION} if a class
         * name is passed that actually exists in the stack trace.
         */
        @Test
        void getValidFullLocationInformation() {
            JavaRuntime runtime = new JavaRuntime();

            Function<String, ?> function = runtime.getRelativeCaller(
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION
            );
            Object result = Callee.execute(function, Callee.class.getName());

            assertThat(result).isInstanceOfSatisfying(StackTraceElement.class, element -> {
                assertThat(element.getClassName()).isEqualTo(RelativeCaller.class.getName());
                assertThat(element.getMethodName()).isEqualTo("getValidFullLocationInformation");
                assertThat(element.getFileName()).isEqualTo(JavaRuntimeTest.class.getSimpleName() + ".java");
                assertThat(element.getLineNumber()).isEqualTo(180);
            });
        }

        /**
         * Verifies that {@code null} is returned and a warning log entry is logged for
         * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION} if a class name
         * is passed that does not exist in the stack trace.
         */
        @Test
        void getInvalidFullLocationInformation() {
            JavaRuntime runtime = new JavaRuntime();

            Function<String, ?> function = runtime.getRelativeCaller(
                OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION
            );
            Object result = Callee.execute(function, "org.tinylog.invalid.Foo");

            assertThat(result).isNull();
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getLevel()).isEqualTo(Level.WARN);
                assertThat(entry.getMessage()).contains("org.tinylog.invalid.Foo");
            });
        }

        /**
         * Verifies that the expected caller class is returned for
         * {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} if
         * a class name is passed that actually exists in the stack
         * trace.
         */
        @Test
        void getValidCallerClass() {
            JavaRuntime runtime = new JavaRuntime();

            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(function, Callee.class.getName());

            assertThat(result).isEqualTo(RelativeCaller.class);
        }

        /**
         * Verifies that {@code null} is returned and a warning log entry is logged for
         * {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} if a class name is passed
         * that does not exist in the stack trace.
         */
        @Test
        void getInvalidCallerClass() {
            JavaRuntime runtime = new JavaRuntime();

            Function<String, ?> function = runtime.getRelativeCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
            Object result = Callee.execute(function, "org.tinylog.invalid.Foo");

            assertThat(result).isNull();
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getLevel()).isEqualTo(Level.WARN);
                assertThat(entry.getMessage()).contains("org.tinylog.invalid.Foo");
            });
        }

        /**
         * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
         * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}.
         *
         * @param outputDetails {@link OutputDetails#DISABLED} or
         *                      {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}
         */
        @ParameterizedTest
        @EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFORMATION"})
        void getDisabledOrWithoutLocationInformation(OutputDetails outputDetails) {
            JavaRuntime runtime = new JavaRuntime();

            Function<String, ?> function = runtime.getRelativeCaller(outputDetails);
            Object result = Callee.execute(function, Callee.class.getName());

            assertThat(result).isNull();
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
