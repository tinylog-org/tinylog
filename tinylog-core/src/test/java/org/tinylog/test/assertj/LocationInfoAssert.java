package org.tinylog.test.assertj;

import java.util.Objects;

import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for location info objects.
 */
public final class LocationInfoAssert extends AbstractAssert<LocationInfoAssert, Object> {

    /**
     * @param actual The actual value to test
     */
    private LocationInfoAssert(Object actual) {
        super(actual, LocationInfoAssert.class);
    }

    /**
     * Creates a new instance of {@link LocationInfoAssert} for testing a location info object.
     *
     * @param location The location info object to test
     * @return The created assertion object
     */
    public static LocationInfoAssert assertThatLocationInfo(Object location) {
        return new LocationInfoAssert(location);
    }

    /**
     * Verifies that the location info object fulfills exactly one of the following requirements.
     *
     * <ul>
     *     <li>
     *         An instance of {@link StackTraceElement} with a class name that is equal to the class name of the passed
     *         expected class
     *     </li>
     *     <li>
     *         An instance of {@link Class} that is equal to the passed expected class
     *     </li>
     *     <li>
     *         A {@link String} that is equal to the fully-qualified class name of passed expected class
     *     </li>
     * </ul>
     *
     * @param expectedClass The expected class
     * @return The current assertion object
     */
    public LocationInfoAssert hasClass(Class<?> expectedClass) {
        isInstanceOfAny(StackTraceElement.class, Class.class, String.class);

        if (actual instanceof StackTraceElement) {
            StackTraceElement actualElement = (StackTraceElement) actual;
            if (Objects.equals(actualElement.getClassName(), expectedClass.getName())) {
                return this;
            }
        } else if (actual instanceof Class<?>) {
            Class<?> actualClass = (Class<?>) actual;
            if (Objects.equals(actualClass, expectedClass)) {
                return this;
            }
        } else if (actual instanceof String) {
            String actualClassName = (String) actual;
            if (Objects.equals(actualClassName, expectedClass.getName())) {
                return this;
            }
        }

        failWithActualExpectedAndMessage(
            actual,
            expectedClass,
            "Expected location info with class <%s>",
            expectedClass
        );

        return this;
    }

    /**
     * Verifies that the location info object is an instance of {@link StackTraceElement} that fulfills all passed
     * expected values.
     *
     * @param expectedClass The expected class
     * @param expectedMethod The expected method name
     * @param expectedFileName The expected file name
     * @param expectedLineNumber The expected line number
     * @return The current assertion object
     */
    public LocationInfoAssert isStackTraceElement(
        Class<?> expectedClass,
        String expectedMethod,
        String expectedFileName,
        int expectedLineNumber
    ) {
        return isInstanceOfSatisfying(StackTraceElement.class, actualElement -> {
            if (!Objects.equals(expectedClass.getName(), actualElement.getClassName())) {
                failWithActualExpectedAndMessage(
                    actualElement.getClassName(),
                    expectedClass.getName(),
                    "Expected class name <%s> to be equal to <%s>",
                    actualElement.getClassName(),
                    expectedClass.getName()
                );
            } else if (!Objects.equals(expectedMethod, actualElement.getMethodName())) {
                failWithActualExpectedAndMessage(
                    actualElement.getMethodName(),
                    expectedMethod,
                    "Expected method name <%s> to be equal to <%s>",
                    actualElement.getMethodName(),
                    expectedMethod
                );
            } else if (!Objects.equals(expectedFileName, actualElement.getFileName())) {
                failWithActualExpectedAndMessage(
                    actualElement.getFileName(),
                    expectedFileName,
                    "Expected line number <%s> to be equal to <%s>",
                    actualElement.getFileName(),
                    expectedFileName
                );
            } else if (expectedLineNumber != actualElement.getLineNumber()) {
                failWithActualExpectedAndMessage(
                    actualElement.getLineNumber(),
                    expectedLineNumber,
                    "Expected line number <%s> to be equal to <%s>",
                    actualElement.getLineNumber(),
                    expectedLineNumber
                );
            }
        });
    }

}
