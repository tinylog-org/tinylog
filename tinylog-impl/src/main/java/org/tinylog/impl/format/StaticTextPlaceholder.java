package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.tinylog.impl.LogEntry;

/**
 * Wrapper for outputting plain static text.
 *
 * <p>
 *     There is no corresponding curly brackets placeholder for this wrapper placeholder. Instead, this wrapper is used
 *     for all plain static text in format patterns.
 * </p>
 */
public class StaticTextPlaceholder implements Placeholder {

	private final String text;

	/**
	 * @param text Plain text to output
	 */
	public StaticTextPlaceholder(String text) {
		this.text = text;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		builder.append(text);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		statement.setString(index, text);
	}

}
