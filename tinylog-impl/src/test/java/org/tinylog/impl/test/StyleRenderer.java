package org.tinylog.impl.test;

import org.tinylog.impl.format.style.Style;

/**
 * Renderer for {@link Style Styles}.
 */
public class StyleRenderer {

	private final Style style;

	/**
	 * @param style The style to render
	 */
	public StyleRenderer(Style style) {
		this.style = style;
	}

	/**
	 * Renders the stored style with the passed text as input.
	 *
	 * @param text The text to style
	 * @return The render result
	 */
	public String render(String text) {
		StringBuilder builder = new StringBuilder(text);
		style.apply(builder, 0);
		return builder.toString();
	}

}
