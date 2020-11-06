package org.tinylog.core.format.message;

/**
 * Message formatter can replace placeholders with real values in strings.
 */
public interface MessageFormatter {

	/**
	 * Replaces all placeholders with real values.
	 *
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	String format(String message, Object... arguments);

}
