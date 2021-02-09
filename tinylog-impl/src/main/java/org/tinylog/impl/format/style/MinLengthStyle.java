package org.tinylog.impl.format.style;

import org.tinylog.core.internal.InternalLogger;

/**
 * Style implementation for setting a configurable minimum length.
 */
public class MinLengthStyle implements Style {

	private final int minLength;
	private final Position position;

	/**
	 * @param minLength The minimum length for the input string
	 * @param position The position for the input string
	 */
	public MinLengthStyle(int minLength, Position position) {
		this.minLength = minLength;
		this.position = position;
	}

	@Override
	public void apply(StringBuilder builder, int start) {
		int totalLength = builder.length();
		int valueLength = totalLength - start;
		int difference = minLength - valueLength;

		if (difference > 0) {
			builder.setLength(totalLength + difference);

			switch (position) {
				case LEFT:
					replaceWithSpaces(builder, totalLength, difference);
					break;
				case CENTER:
					int leftSpaces = difference / 2;
					int rightSpaces = difference - leftSpaces;
					moveString(builder, start, valueLength, leftSpaces);
					replaceWithSpaces(builder, start, leftSpaces);
					replaceWithSpaces(builder, start + leftSpaces + valueLength, rightSpaces);
					break;
				case RIGHT:
					moveString(builder, start, valueLength, difference);
					replaceWithSpaces(builder, start, difference);
					break;
				default:
					InternalLogger.error(null, "Illegal position \"" + position + "\" for min length style");
					break;
			}
		}
	}

	/**
	 * Moves a substring within a string builder.
	 *
	 * @param builder The string builder to modify
	 * @param index The index of the substring to move
	 * @param length The length of the substring to move
	 * @param offset The target position of the substring, relative to the origin index
	 */
	private static void moveString(StringBuilder builder, int index, int length, int offset) {
		String value = builder.substring(index, index + length);
		builder.replace(index + offset, index + offset + length, value);
	}

	/**
	 * Overwrites a sequence in a string builder with space characters.
	 *
	 * @param builder The string builder to modify
	 * @param start The index in the string builder, where to start overwriting
	 * @param count The number of characters to overwrite with a space character
	 */
	private static void replaceWithSpaces(StringBuilder builder, int start, int count) {
		for (int index = start; index < start + count; ++index) {
			builder.setCharAt(index, ' ');
		}
	}

}
