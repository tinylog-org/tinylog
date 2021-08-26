package org.tinylog.impl.format.style;

import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable indentation to all lines from the actual placeholder output.
 */
public class IndentStyle extends AbstractStylePlaceholder {

	private static final String NEW_LINE = System.lineSeparator();

	private final String replacement;

	/**
	 * @param placeholder The actual placeholder to indent
	 * @param replacement The indentation string
	 */
	public IndentStyle(Placeholder placeholder, String replacement) {
		super(placeholder);
		this.replacement = replacement;
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		int index = start;

		boolean indentAtStart = index == 0 && builder.length() > 0
			|| index >= NEW_LINE.length() && NEW_LINE.equals(builder.substring(index - NEW_LINE.length(), index));

		if (indentAtStart) {
			index = indent(builder, index);
		}

		while ((index = builder.indexOf(NEW_LINE, index)) >= 0) {
			index = indent(builder, index + NEW_LINE.length());
		}
	}

	/**
	 * Indents the line at the passed index position in the passed string builder.
	 *
	 * @param builder The string builder that contains a line to indent
	 * @param index The index position of the line to indent
	 * @return The index position of the actual line start after indentation
	 */
	private int indent(StringBuilder builder, int index) {
		builder.insert(index, replacement);
		index += replacement.length();

		while (builder.charAt(index) == '\t') {
			builder.replace(index, index + 1, replacement);
			index += replacement.length();
		}

		return index;
	}

}
