package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

/**
 * Placeholder implementation for resolving the package of the source class for a log entry.
 */
public class PackagePlaceholder implements Placeholder {

	/** */
	public PackagePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.CLASS);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String className = entry.getClassName();
		builder.append(className == null ? "<package unknown>" : extractPackageName(className));
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		String className = entry.getClassName();
		return new SqlRecord<>(SqlType.STRING, className == null ? null : extractPackageName(className));
	}

	/**
	 * Extracts the package name from a fully-qualified class name.
	 *
	 * @param fullyQualifiedClassName The package name is extracted from this fully-qualified class
	 * @return Name of the package
	 */
	private static String extractPackageName(String fullyQualifiedClassName) {
		int lastDot = fullyQualifiedClassName.lastIndexOf('.');
		if (lastDot < 0) {
			return fullyQualifiedClassName;
		} else {
			return fullyQualifiedClassName.substring(0, lastDot);
		}
	}

}
