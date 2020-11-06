package org.tinylog.core.format.value;

/**
 * Format interface for different value types.
 */
public interface ValueFormat {

	/**
	 * Checks if the passed value is supported.
	 *
	 * @param value Value to test
	 * @return {@code true} if the passed value is supported, {@code false} if not
	 */
	boolean isSupported(Object value);

	/**
	 * Formats the passed value.
	 *
	 * @param pattern Format pattern for the value
	 * @param value Value to format
	 * @return Formatted value
	 */
	String format(String pattern, Object value);

}
