package org.tinylog.impl.backend;

/**
 * Resolver for stack trace location information.
 */
final class LocationInfo {

	private static final StackTraceElement EMPTY_STACK_TRACE_ELEMENT = new StackTraceElement(
		"<unknown>",
		"<unknown>",
		null,
		-1
	);

	/** */
	private LocationInfo() {
	}

	/**
	 * Resolves the class name.
	 *
	 * <p>
	 *     Supported arguments: <ol>
	 *         <li>{@link StackTraceElement}</li>
	 *         <li>{@link Class}</li>
	 *         <li>{@link String} with fully-qualified class name</li>
	 *     </ol>
	 * </p>
	 *
	 * <p>
	 *     An empty string will be returned for {@code null} and unsupported types.
	 * </p>
	 *
	 * @param location Location information
	 * @return The fully-qualified class name or an empty string
	 */
	static String resolveClassName(Object location) {
		if (location instanceof StackTraceElement) {
			return ((StackTraceElement) location).getClassName();
		} else if (location instanceof Class) {
			return ((Class<?>) location).getName();
		} else if (location instanceof String) {
			return (String) location;
		} else {
			return "";
		}
	}

	/**
	 * Resolves the stack trace element.
	 *
	 * <p>
	 *     Supported arguments: <ol>
	 *         <li>{@link StackTraceElement}</li>
	 *         <li>{@link Class}</li>
	 *         <li>{@link String} with fully-qualified class name</li>
	 *     </ol>
	 * </p>
	 *
	 * <p>
	 *     The default value is {@code "<unknown>"} for class and method, {@code null} for file, and {@code -1} for
	 *     line. These default values will be used, if the corresponding values cannot be resolved from the passed
	 *     location information.
	 * </p>
	 *
	 * @param location Location information
	 * @return An pre-filled stack trace element
	 */
	static StackTraceElement resolveStackTraceElement(Object location) {
		if (location instanceof StackTraceElement) {
			return (StackTraceElement) location;
		} else if (location instanceof Class) {
			return new StackTraceElement(
				((Class<?>) location).getName(),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getFileName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else if (location instanceof String) {
			return new StackTraceElement(
				(String) location,
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getFileName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else {
			return EMPTY_STACK_TRACE_ELEMENT;
		}
	}

}
