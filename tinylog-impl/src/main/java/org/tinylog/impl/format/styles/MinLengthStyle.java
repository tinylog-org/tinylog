package org.tinylog.impl.format.styles;

import java.util.Arrays;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable minimum length.
 */
public class MinLengthStyle extends AbstractStylePlaceholder {

	private final int minLength;
	private final Position position;

	/**
	 * @param placeholder The actual placeholder to style
	 * @param minLength The minimum length for the input string
	 * @param position The position for the input string
	 */
	public MinLengthStyle(Placeholder placeholder, int minLength, Position position) {
		super(placeholder);
		this.minLength = minLength;
		this.position = position;
	}

	@Override
	protected void apply(StringBuilder builder, int start) {
		int totalLength = builder.length();
		int valueLength = totalLength - start;
		int difference = minLength - valueLength;

		if (difference > 0) {
			switch (position) {
				case LEFT:
					insertSpaces(builder, totalLength, difference);
					break;
				case CENTER:
					int leftSpaces = difference / 2;
					insertSpaces(builder, start, leftSpaces);
					int rightSpaces = difference - leftSpaces;
					insertSpaces(builder, start + leftSpaces + valueLength, rightSpaces);
					break;
				case RIGHT:
					insertSpaces(builder, start, difference);
					break;
				default:
					InternalLogger.error(null, "Illegal position \"" + position + "\" for min length style");
					break;
			}
		}
	}

	/**
	 * Inserts a sequence of space characters into a string builder.
	 *
	 * @param builder The string builder to modify
	 * @param index The index, where to insert the space characters into the string builder
	 * @param count The number of space characters to insert
	 */
	private static void insertSpaces(StringBuilder builder, int index, int count) {
		char[] spaces = new char[count];
		Arrays.fill(spaces, ' ');
		builder.insert(index, spaces);
	}

}
