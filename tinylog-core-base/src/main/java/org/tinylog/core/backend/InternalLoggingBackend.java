package org.tinylog.core.backend;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Internal logging backend that prints internal tinylog errors and warnings to {@link System#err}.
 */
public class InternalLoggingBackend implements LoggingBackend {

	private static final String TAG = "tinylog";

	private static final ContextStorage STORAGE = new NopContextStorage();

	private static final LevelVisibility TAGGED = new LevelVisibility(false, false, false, true, true);
	private static final LevelVisibility DEFAULT = new LevelVisibility(false, false, false, false, false);

	/** */
	public InternalLoggingBackend() {
	}

	@Override
	public ContextStorage getContextStorage() {
		return STORAGE;
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
		if (TAG.equals(tag)) {
			return TAGGED;
		} else {
			return DEFAULT;
		}
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		return TAG.equals(tag) && level.ordinal() <= Level.WARN.ordinal();
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		if (TAG.equals(tag) && level.ordinal() <= Level.WARN.ordinal()) {
			StringBuilder builder = new StringBuilder();

			builder.append("TINYLOG ");
			builder.append(level);
			builder.append(": ");

			if (message != null) {
				if (formatter == null || arguments == null) {
					builder.append(message);
				} else {
					builder.append(formatter.format(message.toString(), arguments));
				}
			}

			if (throwable != null) {
				if (message != null) {
					builder.append(": ");
				}

				StringWriter writer = new StringWriter();
				throwable.printStackTrace(new PrintWriter(writer));
				builder.append(writer);
			} else {
				builder.append(System.lineSeparator());
			}

			System.err.print(builder);
		}
	}

}
