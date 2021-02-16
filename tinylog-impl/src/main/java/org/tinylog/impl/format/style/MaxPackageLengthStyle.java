package org.tinylog.impl.format.style;

import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable maximum length to package names.
 */
public class MaxPackageLengthStyle extends AbstractStylePlaceholder {

	private static final String ELLIPSIS = "...";

	private final int maxLength;

	/**
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the input string
	 */
	public MaxPackageLengthStyle(Placeholder placeholder, int maxLength) {
		super(placeholder);
		this.maxLength = maxLength;
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		if (builder.length() - start > maxLength) {
			shortenPackageSegments(builder, start);

			if (builder.length() - start > maxLength) {
				truncatePackageName(builder, start);
			}
		}
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
	 * @param start The index position of the package name in the passed string builder
	 */
	private void shortenPackageSegments(StringBuilder builder, int start) {
		int readIndex = start;
		int writeIndex = start;

		while (builder.length() + writeIndex - readIndex > maxLength && readIndex < builder.length()) {
			char character = builder.charAt(readIndex);
			builder.setCharAt(writeIndex++, character);

			if (character == '.') {
				readIndex += 1;
			} else {
				int dotIndex = builder.indexOf(".", readIndex);
				readIndex = dotIndex < 0 ? builder.length() : dotIndex;

				while (readIndex < builder.length() && builder.charAt(readIndex) == '.') {
					builder.setCharAt(writeIndex++, '.');
					readIndex += 1;
				}
			}
		}

		builder.delete(writeIndex, readIndex);
	}

	/**
	 * Truncates the entire package name to the defined maximum length.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>org.foo.example -> ....example</code></pre>
	 * </p>
	 *
	 * @param builder The string builder containing a package name
	 * @param start The index position of the package name in the passed string builder
	 */
	private void truncatePackageName(StringBuilder builder, int start) {
		if (maxLength >= ELLIPSIS.length()) {
			int difference = builder.length() - start - maxLength + ELLIPSIS.length();
			int dotIndex = builder.indexOf(".", start + difference);
			int end = dotIndex < 0 ? builder.length() : dotIndex;
			builder.replace(start, end, ELLIPSIS);
		} else {
			builder.setLength(start + maxLength);
		}
	}

}
