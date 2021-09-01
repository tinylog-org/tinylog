package org.tinylog.impl.writers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Builder for creating an instance of {@link FileWriter}.
 */
public class FileWriterBuilder implements WriterBuilder {

	private static final String DEFAULT_PATTERN =
		"{date} [{thread}] {level|min-length:5} {class}.{method}(): {message}";

	private static final String PATTERN_KEY = "pattern";
	private static final String FILE_KEY = "file";
	private static final String CHARSET_KEY = "charset";

	/** */
	public FileWriterBuilder() {
	}

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public Writer create(Framework framework, Configuration configuration) throws IOException {
		String pattern = configuration.getValue(PATTERN_KEY, DEFAULT_PATTERN) + System.lineSeparator();
		Placeholder placeholder = new FormatPatternParser(framework).parse(pattern);

		String fileName = configuration.getValue(FILE_KEY);
		if (fileName == null) {
			String fullKey = configuration.resolveFullKey(FILE_KEY);
			throw new IllegalArgumentException("File name is missing in required property \"" + fullKey + "\"");
		}

		String charsetName = configuration.getValue(CHARSET_KEY);
		Charset charset = StandardCharsets.UTF_8;
		if (charsetName != null) {
			try {
				charset = Charset.forName(charsetName);
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(
					ex,
					"Invalid charset \"{}\" in property \"{}\"",
					charsetName,
					configuration.resolveFullKey(CHARSET_KEY)
				);
			}
		}

		return new FileWriter(placeholder, Paths.get(fileName), charset);
	}

}
