package org.tinylog.impl.format.placeholders;

import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Bundle of multiple child placeholders.
 *
 * <p>
 *     This bundle placeholder combines the render result of multiple child placeholders. All child placeholders are
 *     rendered in the order in which they have been passed.
 * </p>
 */
public class BundlePlaceholder implements Placeholder {

	private final List<Placeholder> placeholders;

	/**
	 * @param placeholders Child placeholders
	 */
	public BundlePlaceholder(List<Placeholder> placeholders) {
		this.placeholders = new ArrayList<>(placeholders);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		Set<LogEntryValue> requiredValues = EnumSet.noneOf(LogEntryValue.class);
		placeholders.forEach(placeholder -> requiredValues.addAll(placeholder.getRequiredLogEntryValues()));
		return requiredValues;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		placeholders.forEach(placeholder -> placeholder.render(builder, entry));
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		StringBuilder builder = new StringBuilder();
		render(builder, entry);
		return new SqlRecord<>(Types.LONGVARCHAR, builder);
	}

}
