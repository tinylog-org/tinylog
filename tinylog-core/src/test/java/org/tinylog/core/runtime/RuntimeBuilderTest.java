package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@RegisterService(
    service = RuntimeBuilder.class,
    implementations = {RuntimeBuilderTest.FirstRuntimeBuilder.class, RuntimeBuilderTest.SecondRuntimeBuilder.class}
)
class RuntimeBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that the runtime flavor with the highest priority is chosen.
     */
    @Test
    void highestPriority() {
        FirstRuntimeBuilder.supported = true;
        FirstRuntimeBuilder.priority = Integer.MAX_VALUE;

        SecondRuntimeBuilder.supported = true;
        SecondRuntimeBuilder.priority = Integer.MIN_VALUE;

        RuntimeBuilder builder = RuntimeBuilder.load(classLoader);
        assertThat(builder).isInstanceOf(FirstRuntimeBuilder.class);
    }

    /**
     * Verifies that only supported runtime flavor are chosen.
     */
    @Test
    void supported() {
        FirstRuntimeBuilder.supported = false;
        FirstRuntimeBuilder.priority = Integer.MAX_VALUE;

        SecondRuntimeBuilder.supported = true;
        SecondRuntimeBuilder.priority = Integer.MIN_VALUE;

        RuntimeBuilder builder = RuntimeBuilder.load(classLoader);
        assertThat(builder).isInstanceOf(SecondRuntimeBuilder.class);
    }

    /**
     * Verifies that an {@link IllegalStateException} will be thrown, if there is no supported runtime flavor.
     */
    @Test
    void unsupported() {
        FirstRuntimeBuilder.supported = false;
        FirstRuntimeBuilder.priority = Integer.MAX_VALUE;

        SecondRuntimeBuilder.supported = false;
        SecondRuntimeBuilder.priority = Integer.MAX_VALUE;

        assertThatCode(() -> RuntimeBuilder.load(classLoader))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("runtime");
    }

    /**
     * First runtime builder service implementation.
     */
    public static class FirstRuntimeBuilder implements RuntimeBuilder {

        private static boolean supported;
        private static int priority;

        @Override
        public boolean isSupported() {
            return supported;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public RuntimeFlavor create(InternalLogger logger) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second runtime builder service implementation.
     */
    public static class SecondRuntimeBuilder implements RuntimeBuilder {

        private static boolean supported;
        private static int priority;

        @Override
        public boolean isSupported() {
            return supported;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public RuntimeFlavor create(InternalLogger logger) {
            throw new UnsupportedOperationException();
        }
    }

}
