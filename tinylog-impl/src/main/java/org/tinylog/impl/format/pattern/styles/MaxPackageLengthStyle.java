package org.tinylog.impl.format.pattern.styles;

import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable maximum length to package names.
 */
public class MaxPackageLengthStyle extends AbstractMaxLengthStyle {

	/**
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the input string
	 */
	public MaxPackageLengthStyle(Placeholder placeholder, int maxLength) {
		super(placeholder, maxLength);
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		if (builder.length() - start > getMaxLength()) {
			shortenPackageSegments(builder, start, builder.length());

			if (builder.length() - start > getMaxLength()) {
				truncatePackageName(builder, start);
			}
		}
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
		if (getMaxLength() >= ELLIPSIS.length()) {
			int difference = builder.length() - start - getMaxLength() + ELLIPSIS.length();
			int dotIndex = builder.indexOf(DOT, start + difference);
			int end = dotIndex < 0 ? builder.length() : dotIndex;
			builder.replace(start, end, ELLIPSIS);
		} else {
			builder.setLength(start + getMaxLength());
		}
	}

}
