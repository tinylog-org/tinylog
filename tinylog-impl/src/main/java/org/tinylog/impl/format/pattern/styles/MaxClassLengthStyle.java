package org.tinylog.impl.format.pattern.styles;

import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable maximum length to fully-qualified class names.
 */
public class MaxClassLengthStyle extends AbstractMaxLengthStyle {

	/**
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the input string
	 */
	public MaxClassLengthStyle(Placeholder placeholder, int maxLength) {
		super(placeholder, maxLength);
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		if (builder.length() - start > getMaxLength()) {
			int end = builder.lastIndexOf(DOT);
			if (end >= start) {
				shortenPackageSegments(builder, start, end);
			}

			if (builder.length() - start > getMaxLength()) {
				truncateClassName(builder, start);
			}
		}
	}

	/**
	 * Truncates the entire fully-qualified class name to the defined maximum length.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>org.foo.example.MyClass -> ....example.MyClass</code></pre>
	 * </p>
	 *
	 * @param builder The string builder containing a fully-qualified class name
	 * @param start The index position of the fully-qualified class name in the passed string builder
	 */
	private void truncateClassName(StringBuilder builder, int start) {
		if (getMaxLength() >= ELLIPSIS.length()) {
			int difference = builder.length() - start - getMaxLength();
			int dotIndex = builder.indexOf(DOT, start + difference + ELLIPSIS.length());

			if (dotIndex >= 0) {
				builder.replace(start, dotIndex, ELLIPSIS);
			} else {
				deletePackage(builder, start);

				int length = builder.length() - start;
				if (length > getMaxLength()) {
					builder.replace(start + getMaxLength() - ELLIPSIS.length(), start + length, ELLIPSIS);
				}
			}
		} else {
			deletePackage(builder, start);
			builder.setLength(start + getMaxLength());
		}
	}

	/**
	 * Deletes the package name and keeps only the simply class name.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>org.foo.example.MyClass -> MyClass</code></pre>
	 * </p>
	 *
	 * @param builder The string builder containing a fully-qualified class name
	 * @param start The index position of the fully-qualified class name in the passed string builder
	 */
	private void deletePackage(StringBuilder builder, int start) {
		int dotIndex = builder.lastIndexOf(DOT);
		if (dotIndex >= start) {
			builder.delete(start, dotIndex + 1);
		}
	}

}
