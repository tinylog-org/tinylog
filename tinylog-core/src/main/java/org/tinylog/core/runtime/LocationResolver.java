package org.tinylog.core.runtime;

/**
 * Resolver for stack trace information.
 *
 * <p>
 *     Supported location types are:
 * </p>
 *
 * <ul>
 *     <li>{@code null}</li>
 *     <li>{@link String}</li>
 *     <li>{@link Class}</li>
 *     <li>{@link StackTraceElement}</li>
 * </ul>
 */
public final class LocationResolver {

    /** */
    private LocationResolver() {
    }

    /**
     * Extracts the fully-qualified name of the class from the passed location object.
     *
     * @param location The source stack trace information
     * @return The fully-qualified source class name or {@code null}
     */
    public static String getClassName(Object location) {
        if (location instanceof StackTraceElement) {
            return normalizeClassName(((StackTraceElement) location).getClassName());
        } else if (location instanceof Class) {
            return normalizeClassName(((Class<?>) location).getName());
        } else if (location instanceof String) {
            return normalizeClassName((String) location);
        } else {
            return null;
        }
    }

    /**
     * Extracts the method name from the passed location object.
     *
     * @param location The source stack trace information
     * @return The method name or {@code null}
     */
    public static String getMethodName(Object location) {
        if (location instanceof StackTraceElement) {
            StackTraceElement stackTraceElement = (StackTraceElement) location;
            return normalizeMethodName(stackTraceElement.getMethodName());
        } else {
            return null;
        }
    }

    /**
     * Extracts the source file name from the passed location object.
     *
     * @param location The source stack trace information
     * @return The source file name or {@code null}
     */
    public static String getFileName(Object location) {
        if (location instanceof StackTraceElement) {
            StackTraceElement stackTraceElement = (StackTraceElement) location;
            return stackTraceElement.getFileName();
        } else {
            return null;
        }
    }

    /**
     * Extracts the line number of the source file from the passed location object.
     *
     * @param location The source stack trace information
     * @return The line number of the source file or {@code -1}
     */
    public static int getLineNumber(Object location) {
        if (location instanceof StackTraceElement) {
            StackTraceElement stackTraceElement = (StackTraceElement) location;
            return stackTraceElement.getLineNumber();
        } else {
            return -1;
        }
    }

    /**
     * Remove class name attachments from anonymous classes and lambdas.
     *
     * @param className Fully-qualified class name
     * @return Normalized fully-qualified class name
     */
    private static String normalizeClassName(String className) {
        int start = Math.max(0, className.lastIndexOf("."));

        for (
            int index = className.indexOf('$', start);
            index >= 0 && index < className.length() - 1;
            index = className.indexOf('$', index + 1)
        ) {
            char character = className.charAt(index + 1);
            if (character == '$' || character >= '0' && character <= '9') {
                return className.substring(0, index);
            }
        }

        return className;
    }

    /**
     * Remove method name attachments from lambdas.
     *
     * @param methodName Method name
     * @return Normalized method name
     */
    private static String normalizeMethodName(String methodName) {
        int firstIndex = methodName.indexOf('$');
        if (firstIndex >= 0) {
            int secondIndex = methodName.indexOf('$', firstIndex + 1);
            if (secondIndex >= 0) {
                return methodName.substring(firstIndex + 1, secondIndex);
            }
        }

        return methodName;
    }

}
