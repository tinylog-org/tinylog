package org.tinylog.impl.writers;

import java.util.Locale;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Builder for creating {@link ConsoleWriter ConsoleWriters}.
 */
public class ConsoleWriterBuilder implements WriterBuilder {

	private static final String PATTERN_KEY = "pattern";
	private static final String DEFAULT_PATTERN =
		"{date} [{thread}] {level|min-length:5} {class}.{method}(): {message}";

	private static final String THRESHOLD_KEY = "threshold";
	private static final Level DEFAULT_THRESHOLD = Level.WARN;

	/** */
	public ConsoleWriterBuilder() {
	}

	@Override
	public String getName() {
		return "console";
	}

	@Override
	public Writer create(Framework framework, Configuration configuration) {
		String pattern = configuration.getValue(PATTERN_KEY, DEFAULT_PATTERN) + System.lineSeparator();
		Placeholder placeholder = new FormatPatternParser(framework).parse(pattern);

		String threshold = configuration.getValue(THRESHOLD_KEY);
		Level level = DEFAULT_THRESHOLD;
		if (threshold != null) {
			try {
				level = Level.valueOf(threshold.toUpperCase(Locale.ENGLISH));
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(
					null,
					"Invalid severity level \"{}\" in property \"{}\"",
					threshold,
					configuration.resolveFullKey(THRESHOLD_KEY)
				);
			}
		}

		return new ConsoleWriter(placeholder, level);
	}

}
