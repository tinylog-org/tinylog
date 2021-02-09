package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

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
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		String className = entry.getClassName();
		statement.setString(index, className == null ? null : extractPackageName(className));
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
