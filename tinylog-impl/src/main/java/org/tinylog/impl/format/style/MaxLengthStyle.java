package org.tinylog.impl.format.style;

import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable maximum length.
 */
public class MaxLengthStyle extends AbstractStylePlaceholder {

	private static final String ELLIPSIS = "...";

	private final int maxLength;

	/**
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the input string
	 */
	public MaxLengthStyle(Placeholder placeholder, int maxLength) {
		super(placeholder);
		this.maxLength = maxLength;
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		int totalLength = builder.length();
		int valueLength = totalLength - start;
		int difference = valueLength - maxLength;

		if (difference > 0) {
			if (maxLength >= ELLIPSIS.length()) {
				builder.setLength(totalLength - difference - ELLIPSIS.length());
				builder.append(ELLIPSIS);
			} else {
				builder.setLength(totalLength - difference);
			}
		}
	}

}
