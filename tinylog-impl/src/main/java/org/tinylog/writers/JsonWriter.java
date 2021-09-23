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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.writers.raw.ByteArrayWriter;

/**
 * Writer for outputting log entries to a log file in JSON format. Already existing files can be continued.
 */
public final class JsonWriter extends AbstractFileBasedWriter {

	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final int BUFFER_SIZE = 1024;
	private static final String FIELD_PREFIX = "field.";

	private final Charset charset;
	private final ByteArrayWriter writer;

	private StringBuilder builder;
	private final Map<String, Token> jsonProperties;

	private final byte[] newLineBytes;
	private final byte[] commaBytes;
	private final byte[] bracketOpenBytes;
	private final byte[] bracketCloseBytes;

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
		super(properties);

		String fileName = getFileName();
		boolean append = getBooleanValue("append");
		boolean buffered = getBooleanValue("buffered");
		boolean writingThread = getBooleanValue("writingthread");

		charset = getCharset();
		writer = createByteArrayWriter(fileName, append, buffered, !writingThread, false, charset);

		byte[] charsetHeader = getCharsetHeader(charset);

		jsonProperties = createTokens(properties);
		newLineBytes = removeHeader(NEW_LINE.getBytes(charset), charsetHeader.length);
		commaBytes = removeHeader(",".getBytes(charset), charsetHeader.length);
		bracketOpenBytes = removeHeader("[".getBytes(charset), charsetHeader.length);
		bracketCloseBytes = removeHeader("]".getBytes(charset), charsetHeader.length);

		if (writingThread) {
			builder = new StringBuilder();
		}

		preProcessFile();
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		StringBuilder builder;
		if (this.builder == null) {
			builder = new StringBuilder();
		} else {
			builder = this.builder;
			builder.setLength(0);
		}

		addJsonObject(logEntry, builder);

		byte[] data = builder.toString().getBytes(charset);
		writer.write(data, 0, data.length);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		writer.flush();
		postProcessFile();
		writer.close();
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Token token : jsonProperties.values()) {
			values.addAll(token.getRequiredLogEntryValues());
		}
		return values;
	}

	/**
	 * Prepares and adds a Json Object. Also escapes special characters.
	 *
	 * @param logEntry LogEntry with information for token
	 * @param builder  Target for the created the JSON object
	 */
	private void addJsonObject(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(NEW_LINE);
		builder.append('\t');
		builder.append("{").append(NEW_LINE);

		Token[] tokenEntries = jsonProperties.values().toArray(new Token[0]);
		String[] fields = jsonProperties.keySet().toArray(new String[0]);

		for (int i = 0; i < tokenEntries.length; i++) {
			builder.append("\t\t\"").append(fields[i]).append("\" : \"");
			int start = builder.length();

			Token token = tokenEntries[i];
			token.render(logEntry, builder);

			escapeCharacter("\\", "\\\\", builder, start);
			escapeCharacter("\"", "\\\"", builder, start);
			escapeCharacter(NEW_LINE, "\\n", builder, start);
			escapeCharacter("\t", "\\t", builder, start);
			escapeCharacter("\b", "\\b", builder, start);
			escapeCharacter("\f", "\\f", builder, start);
			escapeCharacter("\n", "\\n", builder, start);
			escapeCharacter("\r", "\\r", builder, start);

			builder.append("\" ");

			if (i + 1 < jsonProperties.size()) {
				builder.append(",").append(NEW_LINE);
			}
		}
		builder.append(NEW_LINE).append("\t},");
	}

	private void escapeCharacter(final String character, final String escapeWith, final StringBuilder stringBuilder,
			final int startIndex) {
		for (
			int index = stringBuilder.indexOf(character, startIndex);
			index != -1;
			index = stringBuilder.indexOf(character, index + escapeWith.length())
		) {
			stringBuilder.replace(index, index + character.length(), escapeWith);
		}
	}

	private boolean isWhitespace(final byte character) {
		return character == '\n' || character == '\r' || character == ' ';
	}

	/**
	 * Pre-processes the JSON file.
	 *
	 * @throws IOException              Error reading or writing file
	 * @throws IllegalArgumentException Invalid file format
	 */
	private void preProcessFile() throws IOException, IllegalArgumentException {
		byte[] bytes = new byte[BUFFER_SIZE];
		int numberOfBytes = writer.readTail(bytes, 0, BUFFER_SIZE);

		if (numberOfBytes > 0) {
			int sizeToTruncate = 0;
			boolean foundClosingBracket = false;

			for (int i = numberOfBytes - 1; i >= 0; i--) {
				byte letter = bytes[i];
				sizeToTruncate += 1;

				if (letter == ']') {
					foundClosingBracket = true;
				} else if (foundClosingBracket && !isWhitespace(letter)) {
					sizeToTruncate -= 1;
					break;
				}
			}

			if (!foundClosingBracket) {
				throw new IllegalArgumentException(
						"Invalid JSON file. The file is missing a closing bracket for the array.");
			}

			writer.truncate(sizeToTruncate);
			writer.write(commaBytes, 0, commaBytes.length);
		} else {
			writer.write(bracketOpenBytes, 0, bracketOpenBytes.length);
		}
	}

	/**
	 * Post-processes the JSON file. Attempts to delete the trailing comma and
	 * appends closing bracket which were handled in {@link #preProcessFile()}.
	 *
	 * @throws IOException Error writing to file
	 */
	private void postProcessFile() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		int numberOfBytes = writer.readTail(bytes, 0, BUFFER_SIZE);

		if (numberOfBytes > 0 && bytes[numberOfBytes - 1] == ',') {
			writer.truncate(1);
		}

		writer.write(newLineBytes, 0, newLineBytes.length);
		writer.write(bracketCloseBytes, 0, bracketCloseBytes.length);
	}

	private static byte[] removeHeader(final byte[] bytes, final int length) {
		byte[] result = new byte[bytes.length - length];
		System.arraycopy(bytes, length, result, 0, result.length);
		return result;
	}

	private static Map<String, Token> createTokens(final Map<String, String> properties) {
		FormatPatternParser parser = new FormatPatternParser(properties.get("exception"));

		Map<String, Token> tokens = new HashMap<String, Token>();
		for (Entry<String, String> entry : properties.entrySet()) {
			if (entry.getKey().toLowerCase(Locale.ROOT).startsWith(FIELD_PREFIX)) {
				tokens.put(entry.getKey().substring(FIELD_PREFIX.length()), parser.parse(entry.getValue()));
			}
		}
		return tokens;
	}

}
