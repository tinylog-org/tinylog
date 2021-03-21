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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final int BUFFER_SIZE = 1024;
	private static final String FIELD_PREFIX = "field.";

	private Charset charset;
	private ByteArrayWriter writer;
	private FileChannel fileChannel;
	private FileChannel inputChannel;

	private StringBuilder builder;
	private final List<Token> tokens;

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
		tokens = createTokens(properties);
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
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			inputChannel = inputStream.getChannel();
			preprocessFile(append);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

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

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		if (builder == null) {
			builder = new StringBuilder();
		} else {
			builder.setLength(0);
		}
		builder.append("{");
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			token.render(logEntry, builder);
			if (i + 1 < tokens.size()) {
				builder.append(",");
			}
		}
		builder.append("}");
		builder.append(",");

		List<Integer> lineBreakIndexes = new ArrayList<Integer>();
		int lastIndexOf = builder.indexOf(NEW_LINE);
		int currentIndexOf = builder.indexOf(NEW_LINE, lastIndexOf + 1);
		while (currentIndexOf >= 0 && lastIndexOf != currentIndexOf) {
			lineBreakIndexes.add(currentIndexOf);
			lastIndexOf = currentIndexOf;
			currentIndexOf = builder.indexOf(NEW_LINE, currentIndexOf + 1);
		}
		for (int index : lineBreakIndexes) {
			builder.replace(index, index + NEW_LINE.length() + 1, "\\n");
		}
		writer.write(builder.toString().getBytes(charset), builder.length());
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

	/**
	 * Preprocesses the JSON file. If append mode is on, deletes the closing bracket
	 * and adds a comma instead. If it's a new file, appends an opening bracket.
	 * 
	 * @param append Append Mode on or off
	 * @throws IOException              Error reading or writing file
	 * @throws IllegalArgumentException Invalid file format
	 */
	private void preprocessFile(final boolean append) throws IOException, IllegalArgumentException {
		if (append && inputChannel.size() > 0) {
			long sizeToTruncate = 0;
			long currentPosition = inputChannel.size();
			boolean foundClosingBracket = false;
			while (!foundClosingBracket) {
				long from = Math.max(0, currentPosition - BUFFER_SIZE);
				long numberOfBytes = Math.min(currentPosition, BUFFER_SIZE);
				MappedByteBuffer section = inputChannel.map(FileChannel.MapMode.READ_ONLY, from, numberOfBytes);
				byte[] bytes = new byte[section.remaining()];
				section.get(bytes);
				if (bytes.length == 0) {
					throw new IllegalArgumentException(
							"Invalid JSON file. The file is missing a closing bracket for the array.");
				}
				for (int i = bytes.length - 1; i >= 0; i--, currentPosition--) {
					sizeToTruncate += 1;
					if (bytes[i] == ']') {
						foundClosingBracket = true;
						break;
					}
				}
			}
			long newFileSize = fileChannel.size() - sizeToTruncate;
			fileChannel.truncate(newFileSize);
			writer.write(commaByte, 1);
		}

		if (inputChannel.size() == 0) {
			writer.write(bracketOpenByte, 1);
		}
	}

	/**
	 * Postprocesses the JSON file. Attempts to delete the trailing comma and
	 * appends closing bracket which were handled in
	 * {@link #preprocessFile(boolean)}
	 * 
	 * @throws IOException Error writing to file
	 */
	private void postprocessFile() throws IOException {
		fileChannel.truncate(fileChannel.size() - commaByte.length);
		writer.write(NEW_LINE.getBytes(charset), 1);
		writer.write(bracketCloseByte, 1);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Token token : tokens) {
			values.addAll(token.getRequiredLogEntryValues());
		}
		return values;
	}

	private static List<Token> createTokens(final Map<String, String> properties) {
		FormatPatternParser parser = new FormatPatternParser(properties.get("exception"));

		List<Token> tokens = new ArrayList<>();
		for (Entry<String, String> entry : properties.entrySet()) {
			if (entry.getKey().toLowerCase(Locale.ROOT).startsWith(FIELD_PREFIX)) {
				String tokenPattern = new StringBuilder().append("\"").append(entry.getKey().replace(FIELD_PREFIX, ""))
						.append("\":\"{").append(entry.getValue()).append("}\"").toString();
				tokens.add(parser.parse(tokenPattern));
			}
		}
		return tokens;
	}

}
