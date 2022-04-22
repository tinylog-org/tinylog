package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.noneOf(LogEntryValue.class);
	}

	@Override
	public String getValue(LogEntry entry) {
		return text;
	}

	@Override
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		builder.append(text);
	}

}
