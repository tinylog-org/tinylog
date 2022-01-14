package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.writers.AbstractFormattableWriterBuilder;
import org.tinylog.impl.writers.Writer;

/**
 * Builder for creating an instance of {@link FileWriter}.
 */
public class FileWriterBuilder extends AbstractFormattableWriterBuilder {

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
	public Writer create(Framework framework, Configuration configuration, OutputFormat format) throws IOException {
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

		return new FileWriter(format, Paths.get(fileName), charset);
	}

}
