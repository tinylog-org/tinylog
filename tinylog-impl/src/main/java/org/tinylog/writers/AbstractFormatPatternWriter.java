/*
 * Copyright 2016 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.provider.InternalLogger;
import org.tinylog.writers.raw.BufferedWriterDecorator;
import org.tinylog.writers.raw.ByteArrayWriter;
import org.tinylog.writers.raw.OutputStreamWriter;
import org.tinylog.writers.raw.SynchronizedWriterDecorator;

/**
 * Base writer for outputting rendered log entries. The format pattern will be read from property <tt>format</tt>.
 */
public abstract class AbstractFormatPatternWriter implements Writer {

	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final int BUILDER_CAPACITY = 1024;

	private final StringBuilder builder;
	private final Token token;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public AbstractFormatPatternWriter(final Map<String, String> properties) {
		String pattern = properties.get("format");
		if (pattern == null) {
			pattern = DEFAULT_FORMAT_PATTERN;
		}

		token = FormatPatternParser.parse(pattern + NEW_LINE);
		builder = Boolean.parseBoolean(properties.get("writingthread")) ? new StringBuilder(BUILDER_CAPACITY) : null;
	}

	/**
	 * Gets all log entry values that are required for rendering a log entry by the defined format pattern. If a child
	 * writer requires additional log entries, this method has to be overridden.
	 *
	 * @return Required log entry values
	 */
	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return token.getRequiredLogEntryValues();
	}

	/**
	 * Extracts the log file name from configuration.
	 *
	 * @param properties
	 *            Configuration for writer
	 * @return Log file name
	 * @throws IllegalArgumentException
	 *             Log file is not defined in configuration
	 */
	protected static String getFileName(final Map<String, String> properties) {
		String fileName = properties.get("file");
		if (fileName == null) {
			throw new IllegalArgumentException("File name is missing for file writer");
		} else {
			return fileName;
		}
	}

	/**
	 * Extracts the charset from configuration. The default charset will be returned, if no charset is defined or the
	 * defined charset doesn't exist.
	 *
	 * @param properties
	 *            Configuration for writer
	 * @return Configured charset
	 */
	protected static Charset getCharset(final Map<String, String> properties) {
		String charsetName = properties.get("charset");
		try {
			return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
		} catch (IllegalArgumentException ex) {
			InternalLogger.log(Level.ERROR, "Invalid charset: " + charsetName);
			return Charset.defaultCharset();
		}
	}

	/**
	 * Creates a {@link ByteArrayWriter} for a file.
	 *
	 * @param fileName
	 *            Name of file to open for writing
	 * @param append
	 *            An already existing file should be continued
	 * @param buffered
	 *            Output should be buffered
	 * @param threadSafe
	 *            Created writer must be thread-safe
	 * @return Writer for writing to passed file
	 * @throws FileNotFoundException
	 *             File does not exist or cannot be opened for any other reason
	 */
	protected static ByteArrayWriter createByteArrayWriter(final String fileName, final boolean append, final boolean buffered,
		final boolean threadSafe) throws FileNotFoundException {
		File file = new File(fileName).getAbsoluteFile();
		file.getParentFile().mkdirs();

		OutputStream stream = new FileOutputStream(file, append);
		ByteArrayWriter writer = new OutputStreamWriter(stream);

		if (buffered) {
			writer = new BufferedWriterDecorator(writer);
		}

		if (threadSafe) {
			writer = new SynchronizedWriterDecorator(writer, stream);
		}

		return writer;
	}

	/**
	 * Renders a log entry as string.
	 *
	 * @param logEntry
	 *            Log entry to render
	 * @return Rendered log entry
	 */
	protected final String render(final LogEntry logEntry) {
		if (builder == null) {
			StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
			token.render(logEntry, builder);
			return builder.toString();
		} else {
			builder.setLength(0);
			token.render(logEntry, builder);
			return builder.toString();
		}
	}

}
