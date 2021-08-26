package org.tinylog.impl.format.style;

import java.sql.Types;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Style wrapper for other {@link Placeholder Placerholders}.
 */
public abstract class AbstractStylePlaceholder implements Placeholder {

	private final Placeholder placeholder;

	/**
	 * @param placeholder The actual placeholder to style
	 */
	public AbstractStylePlaceholder(Placeholder placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return placeholder.getRequiredLogEntryValues();
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		int start = builder.length();
		placeholder.render(builder, entry);
		apply(builder, start);
	}

	@Override
	public SqlRecord<?> resolve(LogEntry entry) {
		SqlRecord<?> record = placeholder.resolve(entry);
		int originType = record.getType();
		int newType = originType == Types.LONGVARCHAR ? Types.LONGVARCHAR : Types.VARCHAR;
		Object originValue = record.getValue();

		if (originValue == null) {
			return new SqlRecord<>(newType, null);
		} else {
			StringBuilder builder = new StringBuilder();

			if (originType == Types.LONGVARCHAR || originType == Types.VARCHAR) {
				builder.append(originValue);
			} else {
				placeholder.render(builder, entry);
			}

			apply(builder, 0);

			return new SqlRecord<>(newType, builder);
		}
	}

	/**
	 * Applies the style to a {@link StringBuilder} that contains the output from the wrapped placeholder at the passed
	 * index.
	 *
	 * @param builder The string builder that contains the output from the wrapped placeholder
	 * @param start The index position, where the output from the wrapped placeholder starts
	 */
	protected abstract void apply(StringBuilder builder, int start);

}
