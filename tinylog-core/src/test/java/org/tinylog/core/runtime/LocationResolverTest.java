package org.tinylog.core.runtime;

import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LocationResolverTest {

    /**
     * Tests for extract the fully-qualified name of the class.
     */
    @Nested
    class ClassName {

        /**
         * Verifies that the normalized class name can be received from a {@link StackTraceElement}.
         *
         * @param fullSourceClassName The full source class name to pass wrapped in a stack trace element
         * @param expectedNormalizedClassName The expected normalized class name
         */
        @ParameterizedTest
        @CsvSource({
            "org.example.Foo    , org.example.Foo    ",
            "org.example.Foo$Bar, org.example.Foo$Bar",
            "org.example.Foo$bar, org.example.Foo$bar",
            "org.example.Foo$0  , org.example.Foo    ",
            "org.example.Foo$42 , org.example.Foo    ",
            "org.example.Foo$$  , org.example.Foo    "
        })
        void receiveClassNameFromStackTraceElement(String fullSourceClassName, String expectedNormalizedClassName) {
            StackTraceElement element = new StackTraceElement(fullSourceClassName, "bar", null, -1);
            String className = LocationResolver.getClassName(element);
            assertThat(className).isEqualTo(expectedNormalizedClassName);
        }

        /**
         * Verifies that the normalized class name can be received from a {@link String}.
         *
         * @param fullSourceClassName The full source class name to pass as location
         * @param expectedNormalizedClassName The expected normalized class name
         */
        @ParameterizedTest
        @CsvSource({
            "org.example.Foo    , org.example.Foo    ",
            "org.example.Foo$Bar, org.example.Foo$Bar",
            "org.example.Foo$bar, org.example.Foo$bar",
            "org.example.Foo$0  , org.example.Foo    ",
            "org.example.Foo$42 , org.example.Foo    ",
            "org.example.Foo$$  , org.example.Foo    "
        })
        void receiveClassNameFromString(String fullSourceClassName, String expectedNormalizedClassName) {
            String className = LocationResolver.getClassName(fullSourceClassName);
            assertThat(className).isEqualTo(expectedNormalizedClassName);
        }

        /**
         * Verifies that the normalized class name can be received from a first-level class.
         */
        @Test
        void receiveClassNameFromFirstLevelClass() {
            String className = LocationResolver.getClassName(Object.class);
            assertThat(className).isEqualTo("java.lang.Object");
        }

        /**
         * Verifies that the normalized class name can be received from an anonymous class.
         */
        @Test
        void receiveClassNameFromAnonymousClass() {
            Object object = new Object() { };
            String className = LocationResolver.getClassName(object.getClass().getName());
            assertThat(className).isEqualTo(ClassName.class.getName());
        }

        /**
         * Verifies that the normalized class name can be received from a lambda.
         */
        @Test
        void receiveClassNameFromLambda() {
            Runnable lambda = () -> { };
            String className = LocationResolver.getClassName(lambda.getClass().getName());
            assertThat(className).isEqualTo(ClassName.class.getName());
        }

        /**
         * Verifies that {@code null} is passed through as class name.
         */
        @SuppressWarnings("ConstantValue")
        @Test
        void receiveClassNameFromNull() {
            String className = LocationResolver.getClassName(null);
            assertThat(className).isNull();
        }

    }

    /**
     * Tests for extract the method name.
     */
    @Nested
    class MethodName {

        /**
         * Verifies that the normalized synthetic method name can be received from a {@link StackTraceElement}.
         *
         * @param fullSourceMethodName The full method class name to pass wrapped in a stack trace element
         * @param expectedNormalizedMethodName The expected normalized method name
         */
        @ParameterizedTest
        @CsvSource({
            "foo     , foo",
            "$foo$bar, foo"
        })
        void receiveSyntheticMethodNameFromStackTraceElement(
            String fullSourceMethodName,
            String expectedNormalizedMethodName
        ) {
            StackTraceElement element = new StackTraceElement("MyClass", fullSourceMethodName, null, -1);
            String methodName = LocationResolver.getMethodName(element);
            assertThat(methodName).isEqualTo(expectedNormalizedMethodName);
        }

        /**
         * Verifies that the normalized method name of a lambda can be received from a {@link StackTraceElement}.
         */
        @Test
        void receiveLambdaMethodNameFromStackTraceElement() {
            Supplier<StackTraceElement> supplier = () -> new Throwable().getStackTrace()[0];
            String methodName = LocationResolver.getMethodName(supplier.get());
            assertThat(methodName).isEqualTo("receiveLambdaMethodNameFromStackTraceElement");
        }

        /**
         * Verifies that {@code null} is returned as method name for a {@code String}.
         */
        @Test
        void receiveMethodNameFromString() {
            String methodName = LocationResolver.getMethodName("MyClass");
            assertThat(methodName).isNull();
        }

        /**
         * Verifies that {@code null} is returned as method name for a {@code Class}.
         */
        @Test
        void receiveMethodNameFromClass() {
            String methodName = LocationResolver.getMethodName(Object.class);
            assertThat(methodName).isNull();
        }

        /**
         * Verifies that {@code null} is returned as method name for {@code null}.
         */
        @SuppressWarnings("ConstantValue")
        @Test
        void receiveMethodNameFromNull() {
            String methodName = LocationResolver.getMethodName(null);
            assertThat(methodName).isNull();
        }

    }

    /**
     * Tests for extract the source file name.
     */
    @Nested
    class FileName {

        /**
         * Verifies that the file name can be received from a {@link StackTraceElement}.
         */
        @Test
        void receiveFileNameFromStackTraceElement() {
            StackTraceElement element = new StackTraceElement("MyClass", "foo", "MyClass.java", 42);
            String fileName = LocationResolver.getFileName(element);
            assertThat(fileName).isEqualTo("MyClass.java");
        }

        /**
         * Verifies that {@code null} is returned as file name for a {@code String}.
         */
        @Test
        void receiveFileNameFromString() {
            String fileName = LocationResolver.getFileName("MyClass");
            assertThat(fileName).isNull();
        }

        /**
         * Verifies that {@code null} is returned as file name for a {@code Class}.
         */
        @Test
        void receiveFileNameFromClass() {
            String fileName = LocationResolver.getFileName(Object.class);
            assertThat(fileName).isNull();
        }

        /**
         * Verifies that {@code null} is returned as file name for {@code null}.
         */
        @SuppressWarnings("ConstantValue")
        @Test
        void receiveFileNameFromNull() {
            String fileName = LocationResolver.getFileName(null);
            assertThat(fileName).isNull();
        }

    }

    /**
     * Tests for extract the line number of the source file.
     */
    @Nested
    class LineNumber {

        /**
         * Verifies that the line number can be received from a {@link StackTraceElement}.
         */
        @Test
        void receiveLineNumberFromStackTraceElement() {
            StackTraceElement element = new StackTraceElement("MyClass", "foo", "MyClass.java", 42);
            int lineNumber = LocationResolver.getLineNumber(element);
            assertThat(lineNumber).isEqualTo(42);
        }

        /**
         * Verifies that {@code -1} is returned as line number for a {@code String}.
         */
        @Test
        void receiveLineNumberFromString() {
            int lineNumber = LocationResolver.getLineNumber("MyClass");
            assertThat(lineNumber).isEqualTo(-1);
        }

        /**
         * Verifies that {@code -1} is returned as line number for a {@code Class}.
         */
        @Test
        void receiveLineNumberFromClass() {
            int lineNumber = LocationResolver.getLineNumber(Object.class);
            assertThat(lineNumber).isEqualTo(-1);
        }

        /**
         * Verifies that {@code -1} is returned as line number for {@code null}.
         */
        @Test
        void receiveLineNumberFromNull() {
            int lineNumber = LocationResolver.getLineNumber(null);
            assertThat(lineNumber).isEqualTo(-1);
        }

    }

}
