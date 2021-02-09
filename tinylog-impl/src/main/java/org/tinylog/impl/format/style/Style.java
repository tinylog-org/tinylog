package org.tinylog.impl.format.style;

import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Style implementations reformats the output of {@link Placeholder Placeholders}.
 */
public interface Style {

	/**
	 * Applies this style to a substring of a {@link StringBuilder}.
	 *
	 * @param builder The string builder that contains the substring to style
	 * @param start The start position of the substring to style in the passed string builder
	 */
	void apply(StringBuilder builder, int start);

}
