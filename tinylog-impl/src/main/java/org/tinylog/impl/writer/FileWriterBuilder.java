package org.tinylog.impl.writer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating {@link FileWriter FileWriters}.
 */
public class FileWriterBuilder implements WriterBuilder {

	private static final String DEFAULT_PATTERN =
		"{date} [{thread}] {level|min-length:5} {class}.{method}(): {message}";

	/** */
	public FileWriterBuilder() {
	}

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public Writer create(Framework framework, Map<String, String> configuration) throws IOException {
		String pattern = configuration.getOrDefault("pattern", DEFAULT_PATTERN) + System.lineSeparator();
		Placeholder placeholder = new FormatPatternParser(framework).parse(pattern);

		String fileName = configuration.get("file");
		if (fileName == null) {
			throw new IllegalArgumentException("Required property \"file\" is missing for file writer");
		}

		String charsetName = configuration.get("charset");
		Charset charset = StandardCharsets.UTF_8;
		if (charsetName != null) {
			try {
				charset = Charset.forName(charsetName);
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(ex, "Cannot find a supported charset for \"{}\"", charsetName);
			}
		}

		return new FileWriter(placeholder, Paths.get(fileName), charset);
	}

}
