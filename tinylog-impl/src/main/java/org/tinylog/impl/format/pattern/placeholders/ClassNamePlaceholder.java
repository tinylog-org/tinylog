package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the simple class name without package prefix for a log entry.
 */
public class ClassNamePlaceholder implements Placeholder {

	/** */
	public ClassNamePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.CLASS);
	}

	@Override
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public String getValue(LogEntry entry) {
		return extractSimpleClassName(entry.getClassName());
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String simpleClassName = extractSimpleClassName(entry.getClassName());
		builder.append(simpleClassName == null ? "<class unknown>" : simpleClassName);
	}

	/**
	 * Remove the package prefix from a fully-qualified class name.
	 *
	 * @param fullyQualifiedClassName The fully-qualified class name including package prefix
	 * @return The simple class name without package prefix
	 */
	private static String extractSimpleClassName(String fullyQualifiedClassName) {
		if (fullyQualifiedClassName == null) {
			return null;
		} else {
			int lastDot = fullyQualifiedClassName.lastIndexOf('.');
			if (lastDot < 0) {
				return fullyQualifiedClassName;
			} else {
				return fullyQualifiedClassName.substring(lastDot + 1);
			}
		}
	}

}
