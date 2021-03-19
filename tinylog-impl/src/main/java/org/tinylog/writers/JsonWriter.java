/*
 * Copyright 2021 Direnc Timur
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
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
 * Writer for outputting log entries to a log file in JSON format. Already
 * existing files can be continued.
 */
public final class JsonWriter implements Writer {
	private static final String JSON_OBJECT = "{%n%s%n}";
	private static final String MESSAGE_PATTERN = "\"message\": \"{message}\"";
	private static final String TIMESTAMP_PATTERN = "\"timestamp\": \"{date}\"";
	private static final String METHOD_PATTERN = "\"method\": \"{method}()\"";
	private static final String LEVEL_PATTERN = "\"level\": \"{level}\"";
	private static final String CLASS_PATTERN = "\"class\": \"{class}\"";
	private static final String THREAD_PATTERN = "\"thread\": \"{thread}\"";
	private static final String NEW_LINE = System.getProperty("line.separator");

	private Charset charset;
	private ByteArrayWriter writer;
	private FileChannel fileChannel;

	private StringBuilder builder;
	private final Token messageToken;
	private final Token timestampToken;
	private final Token methodToken;
	private final Token levelToken;
	private final Token classToken;
	private final Token threadToken;

	private byte[] commaByte;
	private byte[] bracketOpenByte;
	private byte[] bracketCloseByte;

	/**
	 * @throws IOException              File not found or couldn't access file
	 * @throws IllegalArgumentException Log file is not defined in configuration
	 */
	public JsonWriter() throws IOException {
		this(Collections.<String, String>emptyMap());
	}

	/**
	 * @param properties Configuration for writer
	 * @throws IOException              File not found or couldn't access file
	 * @throws IllegalArgumentException Log file is not defined in configuration
	 */
	public JsonWriter(final Map<String, String> properties) throws IOException {

		String exceptionFilter = properties.get("exception");
		messageToken = new FormatPatternParser(exceptionFilter).parse(MESSAGE_PATTERN);
		timestampToken = new FormatPatternParser(exceptionFilter).parse(TIMESTAMP_PATTERN);
		methodToken = new FormatPatternParser(exceptionFilter).parse(METHOD_PATTERN);
		levelToken = new FormatPatternParser(exceptionFilter).parse(LEVEL_PATTERN);
		classToken = new FormatPatternParser(exceptionFilter).parse(CLASS_PATTERN);
		threadToken = new FormatPatternParser(exceptionFilter).parse(THREAD_PATTERN);

		charset = getCharset(properties);
		commaByte = ",".getBytes(charset);
		bracketOpenByte = "[".getBytes(charset);
		bracketCloseByte = "]".getBytes(charset);

		String fileName = getFileName(properties);
		File file = new File(fileName).getAbsoluteFile();
		file.getParentFile().mkdirs();

		boolean append = Boolean.parseBoolean(properties.get("append"));
		FileOutputStream stream = new FileOutputStream(file, append);
		fileChannel = stream.getChannel();

		writer = new OutputStreamWriter(stream);

		boolean buffered = Boolean.parseBoolean(properties.get("buffered"));
		if (buffered) {
			writer = new BufferedWriterDecorator(writer);
		}

		boolean writingThread = Boolean.parseBoolean(properties.get("writingthread"));
		if (!writingThread) {
			writer = new SynchronizedWriterDecorator(writer, stream);
		}

		preprocessFile(append);
	}

	private Charset getCharset(final Map<String, String> properties) {
		String charsetName = properties.get("charset");
		try {
			return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
		} catch (IllegalArgumentException ex) {
			InternalLogger.log(Level.ERROR, "Invalid charset: " + charsetName);
			return Charset.defaultCharset();
		}
	}

	private String getFileName(final Map<String, String> properties) {
		String fileName = properties.get("file");
		if (fileName == null) {
			throw new IllegalArgumentException("File name is missing for file writer");
		}
		return fileName;
	}

	private void writeProperty(final Token token, final LogEntry logEntry, final StringBuilder builder,
			final boolean hasPrevious) {
		if (hasPrevious) {
			builder.append(String.format(",%s", NEW_LINE));
		}
		token.render(logEntry, builder);
	}

	private String buildJsonLogEntry(final LogEntry logEntry, final StringBuilder builder) {
		boolean hasMessage = logEntry.getMessage() != null;
		boolean hasTimestamp = logEntry.getTimestamp() != null;
		boolean hasLevel = logEntry.getLevel() != null;
		boolean hasMethod = logEntry.getMethodName() != null;
		boolean hasClass = logEntry.getClassName() != null;
		boolean hasThread = logEntry.getThread() != null;

		if (hasMessage) {
			writeProperty(messageToken, logEntry, builder, false);
		}
		if (hasTimestamp) {
			writeProperty(timestampToken, logEntry, builder, hasMessage);
		}
		if (hasLevel) {
			writeProperty(levelToken, logEntry, builder, hasTimestamp);
		}
		if (hasClass) {
			writeProperty(classToken, logEntry, builder, hasLevel);
		}
		if (hasMethod) {
			writeProperty(methodToken, logEntry, builder, hasClass);
		}
		if (hasThread) {
			writeProperty(threadToken, logEntry, builder, hasMethod);
		}
		return String.format(JSON_OBJECT, builder.toString());
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		String jsonLogEntry;
		if (builder == null) {
			builder = new StringBuilder();
		} else {
			builder.setLength(0);
		}
		jsonLogEntry = buildJsonLogEntry(logEntry, builder);
		jsonLogEntry += ",";
		writer.write(jsonLogEntry.getBytes(charset), jsonLogEntry.length());
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		postprocessFile();
		writer.close();
	}

	private void preprocessFile(final boolean append) throws IOException {
		if (append && fileChannel.size() > bracketCloseByte.length) {
			fileChannel.truncate(fileChannel.size() - bracketCloseByte.length);
			writer.write(commaByte, commaByte.length);
		}
		if (fileChannel.size() == 0) {
			writer.write(bracketOpenByte, bracketOpenByte.length);
		}
	}

	private void postprocessFile() throws IOException {
		fileChannel.truncate(fileChannel.size() - commaByte.length);
		writer.write(bracketCloseByte, bracketCloseByte.length);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.noneOf(LogEntryValue.class);
	}

}
