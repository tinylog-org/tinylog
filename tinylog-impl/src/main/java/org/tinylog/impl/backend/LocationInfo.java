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
			return normalizeClassName(((StackTraceElement) location).getClassName());
		} else if (location instanceof Class) {
			return normalizeClassName(((Class<?>) location).getName());
		} else if (location instanceof String) {
			return normalizeClassName((String) location);
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
			StackTraceElement stackTraceElement = (StackTraceElement) location;
			String originalClassName = stackTraceElement.getClassName();
			String normalizedClassName = normalizeClassName(originalClassName);
			String originalMethodName = stackTraceElement.getMethodName();
			String normalizedMethodName = normalizeMethodName(originalMethodName);
			if (originalClassName.equals(normalizedClassName) && originalMethodName.equals(normalizedMethodName)) {
				return stackTraceElement;
			} else {
				return new StackTraceElement(
					normalizedClassName,
					normalizedMethodName,
					stackTraceElement.getFileName(),
					stackTraceElement.getLineNumber()
				);
			}
		} else if (location instanceof Class) {
			return new StackTraceElement(
				normalizeClassName(((Class<?>) location).getName()),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getFileName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else if (location instanceof String) {
			return new StackTraceElement(
				normalizeClassName((String) location),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getFileName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else {
			return EMPTY_STACK_TRACE_ELEMENT;
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
