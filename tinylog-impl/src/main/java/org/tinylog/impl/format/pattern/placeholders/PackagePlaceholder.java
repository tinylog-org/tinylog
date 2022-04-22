package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public String getValue(LogEntry entry) {
		String className = entry.getClassName();
		return className == null ? null : extractPackageName(className);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String className = entry.getClassName();
		if (className == null) {
			builder.append("<package unknown>");
		} else {
			String packageName = extractPackageName(className);
			if (packageName != null) {
				builder.append(packageName);
			}
		}
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
			return null;
		} else {
			return fullyQualifiedClassName.substring(0, lastDot);
		}
	}

}
