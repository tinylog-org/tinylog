package org.tinylog.impl.format.style;

import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Abstract base style wrapper for applying a configurable maximum length.
 */
public abstract class AbstractMaxLengthStyle extends AbstractStylePlaceholder {

	/**
	 * String with ellipsis "{@code ...}" aka dot-dot-dot for omissions.
	 */
	protected static final String ELLIPSIS = "...";

	/**
	 * String with single dot character "{@code .}" that separates package segments.
	 */
	protected static final String DOT = ".";

	/**
	 * Dot character '{@code .}' that separates package segments.
	 */
	protected static final char DOT_CHARACTER = '.';

	private final int maxLength;

	/**
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the input string
	 */
	public AbstractMaxLengthStyle(Placeholder placeholder, int maxLength) {
		super(placeholder);
		this.maxLength = maxLength;
	}

	/**
	 * Gets the configured maximum length.
	 *
	 * @return The configured maximum length
	 */
	protected final int getMaxLength() {
		return maxLength;
	}

	/**
	 * Shortens packet segments to single letters until the entire packet name is no longer than the defined maximum
	 * length.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>org.foo.example -> o.f.example</code></pre>
	 * </p>
	 *
	 * @param builder The string builder containing a package name
	 * @param start The start index of the package name in the passed string builder (inclusive)
	 * @param end The end index of the package name in the passed string builder (exclusive)
	 */
	protected void shortenPackageSegments(StringBuilder builder, int start, int end) {
		int readIndex = start;
		int writeIndex = start;

		while (builder.length() + writeIndex - readIndex > maxLength && readIndex < end) {
			char character = builder.charAt(readIndex);
			builder.setCharAt(writeIndex++, character);

			if (character == DOT_CHARACTER) {
				readIndex += 1;
			} else {
				int dotIndex = builder.indexOf(DOT, readIndex);
				readIndex = dotIndex < 0 ? end : dotIndex;

				while (readIndex < end && builder.charAt(readIndex) == DOT_CHARACTER) {
					builder.setCharAt(writeIndex++, DOT_CHARACTER);
					readIndex += 1;
				}
			}
		}

		builder.delete(writeIndex, readIndex);
	}

}
