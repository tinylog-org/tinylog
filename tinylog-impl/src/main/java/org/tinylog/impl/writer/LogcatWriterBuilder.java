package org.tinylog.impl.writer;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.style.MaxLengthStyleBuilder;

/**
 * Builder for creating {@link LogcatWriter LogcatWriters}.
 */
public class LogcatWriterBuilder implements WriterBuilder {

	private static final int MAX_TAG_LENGTH = 23;

	private static final String DEFAULT_TAG_PATTERN = null;
	private static final String DEFAULT_MESSAGE_PATTERN = "{message}";

	/** */
	public LogcatWriterBuilder() {
	}

	@Override
	public String getName() {
		return "logcat";
	}

	@Override
	public Writer create(Framework framework, Configuration configuration) {
		FormatPatternParser formatPatternParser = new FormatPatternParser(framework);

		String tagPattern = configuration.getValue("tag-pattern", DEFAULT_TAG_PATTERN);
		Placeholder tagPlaceholder = null;
		if (tagPattern != null) {
			Placeholder placeholder = formatPatternParser.parse(tagPattern);
			MaxLengthStyleBuilder maxLengthStyleBuilder = new MaxLengthStyleBuilder();
			tagPlaceholder = maxLengthStyleBuilder.create(framework, placeholder, Integer.toString(MAX_TAG_LENGTH));
		}

		String messagePattern = configuration.getValue("message-pattern", DEFAULT_MESSAGE_PATTERN);
		Placeholder messagePlaceholder = formatPatternParser.parse(messagePattern);

		return new LogcatWriter(tagPlaceholder, messagePlaceholder);
	}

}
