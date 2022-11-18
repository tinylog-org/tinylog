package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.JRE;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyStackTraceAccessTest {

    /**
     * Verifies that {@code sun.reflect.Reflection.getCallerClass(int)} is available on Java 10 and earlier.
     */
    @EnabledForJreRange(max = JRE.JAVA_10)
    @Test
    void sunReflectionAvailable() throws Throwable {
        MethodHandle handle = new LegacyStackTraceAccess().getCallerClassGetter();
        assertThat(handle).isNotNull();

        Object result = handle.invoke(1);
        assertThat(result).isEqualTo(LegacyStackTraceAccessTest.class);
    }

    /**
     * Verifies that {@code sun.reflect.Reflection.getCallerClass(int)} is not available on Java 11 and later.
     */
    @EnabledForJreRange(min = JRE.JAVA_11)
    @Test
    void sunReflectionUnavailableSinceJava11() {
        assertThat(new LegacyStackTraceAccess().getCallerClassGetter()).isNull();
    }

    /**
     * Verifies that {@code sun.reflect.Reflection.getCallerClass(int)} is not available on Android.
     */
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    @Test
    void sunReflectionUnavailableOnAndroid() {
        assertThat(new LegacyStackTraceAccess().getCallerClassGetter()).isNull();
    }

    /**
     * Verifies that {@code Throwable.getStackTraceElement(int)} is available on Java 8 and earlier.
     */
    @EnabledForJreRange(max = JRE.JAVA_8)
    @Test
    void stackTraceElementGetterAvailable() throws Throwable {
        MethodHandle handle = new LegacyStackTraceAccess().getStackTraceElementGetter();
        assertThat(handle).isNotNull();

        Object result = handle.invoke(new Throwable(), 0);
        assertThat(result).isEqualTo(new StackTraceElement(
            LegacyStackTraceAccessTest.class.getName(),
            "stackTraceElementGetterAvailable",
            LegacyStackTraceAccessTest.class.getSimpleName() + ".java",
            54
        ));
    }

    /**
     * Verifies that {@code Throwable.getStackTraceElement(int)} is not available on Java 9 and later.
     */
    @EnabledForJreRange(min = JRE.JAVA_9)
    @Test
    void stackTraceElementGetterUnavailable() {
        assertThat(new LegacyStackTraceAccess().getStackTraceElementGetter()).isNull();
    }

}
