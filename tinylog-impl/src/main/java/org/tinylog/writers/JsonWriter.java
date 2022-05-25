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

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.provider.InternalLogger;
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
	private final Map<String, Token> fields;
	private final boolean lineDelimitedJson;

	private StringBuilder builder;
	private boolean firstEntry;
	private int truncateSize;

	private final byte[] charsetHeaderBytes;
	private final byte[] lineFeedBytes;
	private final byte[] carriageReturnBytes;
	private final byte[] newLineBytes;
	private final byte[] spaceBytes;
	private final byte[] tabulatorBytes;
	private final byte[] commaBytes;
	private final byte[] bracketOpenBytes;
	private final byte[] bracketCloseBytes;

	private final int characterSize;

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
		String format = getStringValue("format");
		boolean append = getBooleanValue("append");
		boolean buffered = getBooleanValue("buffered");
		boolean writingThread = getBooleanValue("writingthread");

		charset = getCharset();
		writer = createByteArrayWriter(fileName, append, buffered, false, false, charset);
		fields = createTokens(properties);

		if (format == null || "JSON".equalsIgnoreCase(format)) {
			lineDelimitedJson = false;
		} else if ("LDJSON".equalsIgnoreCase(format)) {
			lineDelimitedJson = true;
		} else {
			lineDelimitedJson = false;
			InternalLogger.log(Level.WARN, "Illegal format for JSON writer: " + format);
		}

		charsetHeaderBytes = getCharsetHeader(charset);
		lineFeedBytes = removeHeader("\n".getBytes(charset), charsetHeaderBytes.length);
		carriageReturnBytes = removeHeader("\r".getBytes(charset), charsetHeaderBytes.length);
		newLineBytes = removeHeader(NEW_LINE.getBytes(charset), charsetHeaderBytes.length);
		spaceBytes = removeHeader(" ".getBytes(charset), charsetHeaderBytes.length);
		tabulatorBytes = removeHeader("\t".getBytes(charset), charsetHeaderBytes.length);
		commaBytes = removeHeader(",".getBytes(charset), charsetHeaderBytes.length);
		bracketOpenBytes = removeHeader("[".getBytes(charset), charsetHeaderBytes.length);
		bracketCloseBytes = removeHeader("]".getBytes(charset), charsetHeaderBytes.length);

		characterSize = lineFeedBytes.length;
		if (characterSize != carriageReturnBytes.length || characterSize != spaceBytes.length
			|| characterSize != tabulatorBytes.length || characterSize != commaBytes.length
			|| characterSize != bracketOpenBytes.length || characterSize != bracketCloseBytes.length) {
			throw new IllegalArgumentException("Invalid charset " + charset.displayName() + ". All ASCII characters"
				+ " must have the same number of bytes.");
		}

		if (writingThread) {
			builder = new StringBuilder();
		}

		firstEntry = lineDelimitedJson || prepareStandardJsonFile();
		truncateSize = 0;
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		if (builder == null) {
			StringBuilder builder = new StringBuilder();
			addJsonObject(logEntry, builder);
			synchronized (writer) {
				internalWrite(builder.toString().getBytes(charset));
			}
		} else {
			builder.setLength(0);
			addJsonObject(logEntry, builder);
			internalWrite(builder.toString().getBytes(charset));
		}
	}

	@Override
	public void flush() throws IOException {
		if (builder == null) {
			synchronized (writer) {
				internalFlush();
			}
		} else {
			internalFlush();
		}
	}

	@Override
	public void close() throws IOException {
		if (builder == null) {
			synchronized (writer) {
				internalClose();
			}
		} else {
			internalClose();
		}
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Token token : fields.values()) {
			values.addAll(token.getRequiredLogEntryValues());
		}
		return values;
	}

	/**
	 * Prepares and adds a Json Object. Special characters will be escaped.
	 *
	 * @param logEntry LogEntry with information for token
	 * @param builder  Target for the created the JSON object
	 */
	private void addJsonObject(final LogEntry logEntry, final StringBuilder builder) {
		if (!lineDelimitedJson) {
			builder.append(NEW_LINE);
			builder.append('\t');
		}

		builder.append("{");

		if (!lineDelimitedJson) {
			builder.append(NEW_LINE);
		}

		Token[] tokenEntries = fields.values().toArray(new Token[0]);
		String[] fields = this.fields.keySet().toArray(new String[0]);

		for (int i = 0; i < tokenEntries.length; i++) {
			if (!lineDelimitedJson) {
				builder.append("\t\t");
			}

			builder.append('\"');
			builder.append(fields[i]);
			builder.append("\": \"");
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

			builder.append('"');

			if (i + 1 < this.fields.size()) {
				builder.append(",");

				if (lineDelimitedJson) {
					builder.append(' ');
				} else {
					builder.append(NEW_LINE);
				}
			}
		}

		if (!lineDelimitedJson) {
			builder.append(NEW_LINE).append('\t');
		}

		builder.append('}');

		if (lineDelimitedJson) {
			builder.append(NEW_LINE);
		}
	}

	/**
	 * Outputs a passed byte array unsynchronized.
	 *
	 * <p>
	 *     A comma will be added if there are already other log entries.
	 * </p>
	 *
	 * @param data
	 *            Byte array to output
	 * @throws IOException
	 *             Writing failed
	 */
	private void internalWrite(final byte[] data) throws IOException {
		if (truncateSize > 0) {
			writer.truncate(truncateSize);
			truncateSize = 0;
		}

		if (firstEntry) {
			firstEntry = false;
		} else if (!lineDelimitedJson) {
			writer.write(commaBytes, 0, commaBytes.length);
		}

		writer.write(data, 0, data.length);
	}

	/**
	 * Outputs buffered log entries immediately and close the JSON array unsynchronized.
	 *
	 * @throws IOException Closing failed
	 */
	private void internalFlush() throws IOException {
		if (!lineDelimitedJson) {
			writer.write(newLineBytes, 0, newLineBytes.length);
			writer.write(bracketCloseBytes, 0, bracketCloseBytes.length);
		}

		writer.flush();

		truncateSize = lineDelimitedJson ? 0 : newLineBytes.length + bracketCloseBytes.length;
	}

	/**
	 * Closes the writer unsynchronized.
	 *
	 * @throws IOException Closing failed
	 */
	private void internalClose() throws IOException {
		internalFlush();
		writer.close();
	}

	/**
	 * Replaces a character by its replacement everywhere in a string builder, starting at the given index.
	 *
	 * @param character The character to replace
	 * @param replacement The replacement for the given character
	 * @param builder The string builder to change
	 * @param startIndex The index in the string builder to start at
	 */
	private void escapeCharacter(final String character, final String replacement, final StringBuilder builder,
			final int startIndex) {
		for (
			int index = builder.indexOf(character, startIndex);
			index != -1;
			index = builder.indexOf(character, index + replacement.length())
		) {
			builder.replace(index, index + character.length(), replacement);
		}
	}

	/**
	 * Checks if the passed byte array contains at the passed index a new line "\n", carriage return "\r", space " ", or
	 * tabulator "\t".
	 *
	 * @param data The byte array that contains the character to check
	 * @param index The index of the character to check
	 * @return {@code true} if the bytes at the passed index represents one of the four whitespace characters, otherwise
	 *         {@code false}
	 */
	private boolean isWhitespace(final byte[] data, final int index) {
		return isPresent(data, index, lineFeedBytes)
			|| isPresent(data, index, carriageReturnBytes)
			|| isPresent(data, index, spaceBytes)
			|| isPresent(data, index, tabulatorBytes);
	}

	/**
	 * Checks if the passed byte array contains at the passed index a give character.
	 *
	 * @param data The byte array that contains the character to check
	 * @param index The index of the character to check
	 * @param character The byte representation of the expected character
	 * @return {@code true} if the bytes at the passed index represents the given character, otherwise {@code false}
	 */
	private boolean isPresent(final byte[] data, final int index, final byte[] character) {
		for (int i = 0; i < character.length; ++i) {
			if (data[index + i] != character[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Preparse the content of a standard JSON file for writing log entries.
	 *
	 * @return {@code true} if the JSON file is empty, {@code false} if it contains already log entries
	 * @throws IOException Failed to access the JSON file
	 */
	private boolean prepareStandardJsonFile() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		int numberOfBytes = writer.readTail(bytes, 0, BUFFER_SIZE);

		if (numberOfBytes > charsetHeaderBytes.length) {
			int whitespaceIndex = numberOfBytes;
			boolean foundClosingBracket = false;

			for (int i = numberOfBytes - characterSize; i >= charsetHeaderBytes.length; i -= characterSize) {
				if (isPresent(bytes, i, bracketCloseBytes)) {
					foundClosingBracket = true;
				} else if (foundClosingBracket) {
					if (isWhitespace(bytes, i)) {
						whitespaceIndex = i;
					} else {
						writer.truncate(numberOfBytes - whitespaceIndex);
						return isPresent(bytes, i, bracketOpenBytes);
					}
				}
			}

			throw new IOException("Invalid JSON file. The file is missing a closing bracket for the array.");
		} else {
			writer.write(bracketOpenBytes, 0, bracketOpenBytes.length);
			return true;
		}
	}

	/**
	 * Removes potential charset header (like BOM) from a byte array.
	 *
	 * @param bytes The source byte array
	 * @param length The charset header length in bytes
	 * @return Copy of the source byte array without charset header
	 */
	private static byte[] removeHeader(final byte[] bytes, final int length) {
		byte[] result = new byte[bytes.length - length];
		System.arraycopy(bytes, length, result, 0, result.length);
		return result;
	}

	/**
	 * Creates the token for all fields.
	 *
	 * @param properties The configuration for the {@link JsonWriter}
	 * @return All field names mapped to their tokens
	 */
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
