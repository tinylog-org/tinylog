package org.tinylog.impl.writers.logcat;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.writers.Writer;

import android.util.Log;

/**
 * Synchronous writer that passes log entries to logcat via {@link Log} on Android devices.
 */
public class LogcatWriter implements Writer {

	private static final int BUILDER_CAPACITY = 1024;

	private final Placeholder tagPlaceholder;
	private final Placeholder messagePlaceholder;

	/**
	 * @param tagPlaceholder The placeholder for rendering the tag (can be {@code null})
	 * @param messagePlaceholder The placeholder for rendering the message (must not be {@code null})
	 */
	public LogcatWriter(Placeholder tagPlaceholder, Placeholder messagePlaceholder) {
		this.tagPlaceholder = tagPlaceholder;
		this.messagePlaceholder = messagePlaceholder;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		Set<LogEntryValue> values = EnumSet.of(LogEntryValue.LEVEL);
		if (tagPlaceholder != null) {
			values.addAll(tagPlaceholder.getRequiredLogEntryValues());
		}
		values.addAll(messagePlaceholder.getRequiredLogEntryValues());
		return values;
	}

	@Override
	public void log(LogEntry entry) {
		StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
		String tag = renderTag(builder, entry);

		builder.setLength(0);
		String message = renderMessage(builder, entry);

		switch (entry.getSeverityLevel()) {
			case TRACE:
				Log.println(Log.VERBOSE, tag, message);
				break;
			case DEBUG:
				Log.println(Log.DEBUG, tag, message);
				break;
			case INFO:
				Log.println(Log.INFO, tag, message);
				break;
			case WARN:
				Log.println(Log.WARN, tag, message);
				break;
			case ERROR:
				Log.println(Log.ERROR, tag, message);
				break;
			default:
				InternalLogger.error(null, "Severity level \"{}\" is unsupported", entry.getSeverityLevel());
		}
	}

	@Override
	public void close() {
		// Ignore
	}

	/**
	 * Renders the tag for logcat.
	 *
	 * @param builder The string builder to use for rendering
	 * @param entry The log entry to render
	 * @return The rendered tag or {@code null} if there is no tag placeholder
	 */
	private String renderTag(StringBuilder builder, LogEntry entry) {
		if (tagPlaceholder == null) {
			return null;
		} else {
			tagPlaceholder.render(builder, entry);
			return builder.toString();
		}
	}

	/**
	 * Renders the message for logcat.
	 *
	 * @param builder The string builder to use for rendering
	 * @param entry The log entry to render
	 * @return The rendered message
	 */
	private String renderMessage(StringBuilder builder, LogEntry entry) {
		messagePlaceholder.render(builder, entry);
		return builder.toString();
	}

}
