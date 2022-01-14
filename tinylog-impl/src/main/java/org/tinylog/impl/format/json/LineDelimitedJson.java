package org.tinylog.impl.format.json;

import java.util.EnumSet;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Output format for rendering log entries as line-delimited JSON.
 */
public class LineDelimitedJson implements OutputFormat {

	private final Map<String, Placeholder> fields;
	private final Formatter formatter;

	/**
	 * @param fields The fields to render (the key is used as field name and the placeholder as field value)
	 */
	public LineDelimitedJson(Map<String, Placeholder> fields) {
		this.fields = new LinkedHashMap<>(fields);
		this.formatter = new Formatter(Locale.ROOT);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		Set<LogEntryValue> requiredValues = EnumSet.noneOf(LogEntryValue.class);
		fields.values().forEach(placeholder -> requiredValues.addAll(placeholder.getRequiredLogEntryValues()));
		return requiredValues;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		boolean first = true;

		builder.append("{");

		for (Entry<String, Placeholder> field : fields.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}

			builder.append("\"");
			appendEscaped(builder, field.getKey());
			builder.append("\": \"");
			renderEscaped(builder, field.getValue(), entry);
			builder.append("\"");
		}

		builder.append("}");
		builder.append(System.lineSeparator());
	}

	/**
	 * Appends a text as escaped JSON string to a string builder.
	 *
	 * @param builder The escaped JSON string will be added to this string builder
	 * @param text The text to escape and to append to the passed string builder
	 */
	private void appendEscaped(StringBuilder builder, String text) {
		builder.ensureCapacity(builder.length() + text.length());

		for (int i = 0; i < text.length(); ++i) {
			char character = text.charAt(i);
			String escaped = escape(character);

			if (escaped == null) {
				builder.append(character);
			} else {
				builder.append(escaped);
			}
		}
	}

	/**
	 * Renders a {@link Placeholder} and add its result as escaped JSON string to a string builder.
	 *
	 * @param builder The escaped JSON string result will be added to this string builder
	 * @param placeholder The placeholder to render
	 * @param entry The log entry to render by the passed placeholder
	 */
	private void renderEscaped(StringBuilder builder, Placeholder placeholder, LogEntry entry) {
		int start = builder.length();
		placeholder.render(builder, entry);

		for (int i = start; i < builder.length(); ++i) {
			char character = builder.charAt(i);
			String escaped = escape(character);

			if (escaped != null) {
				String remaining = builder.substring(i + 1);
				builder.setLength(i);
				builder.append(escaped);
				appendEscaped(builder, remaining);
				break;
			}
		}
	}

	/**
	 * Gets the escaped JSON string representation for any character.
	 *
	 * @param character Any character
	 * @return The escaped JSON string representation or {@code null} if no escaping is required
	 */
	private String escape(char character) {
		switch (character) {
			case '"':
				return "\\\"";
			case '\\':
				return "\\\\";
			case '\b':
				return "\\b";
			case '\f':
				return "\\f";
			case '\n':
				return "\\n";
			case '\r':
				return "\\r";
			case '\t':
				return "\\t";
			default:
				if (Character.isISOControl(character)) {
					return formatter.format("\\u%04X", (short) character).toString();
				} else {
					return null;
				}
		}
	}

}
