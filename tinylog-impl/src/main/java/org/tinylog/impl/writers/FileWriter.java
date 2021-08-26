package org.tinylog.impl.writers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Asynchronous writer for writing log entries to a file.
 */
public class FileWriter implements AsyncWriter {

	private static final int BUILDER_START_CAPACITY = 1024;
	private static final int BUILDER_MAX_CAPACITY = 65536;
	private static final int CHUNK_CAPACITY = 65536;

	private final Placeholder placeholder;
	private final RandomAccessFile file;
	private final CharsetEncoder encoder;

	private StringBuilder builder;

	private final byte[] chunk;
	private int chunkMaxSize;
	private int chunkSize;

	/**
	 * @param placeholder The placeholder for formatting log entries
	 * @param file The path to the target log file
	 * @param charset The charset to use for writing strings to the target file
	 * @throws IOException Failed to access the target log file
	 */
	public FileWriter(Placeholder placeholder, Path file, Charset charset) throws IOException {
		Path parent = file.toAbsolutePath().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		this.placeholder = placeholder;
		this.file = new RandomAccessFile(file.toString(), "rw");
		this.encoder = charset.newEncoder()
			.onUnmappableCharacter(CodingErrorAction.REPLACE)
			.onMalformedInput(CodingErrorAction.REPLACE);

		this.builder = new StringBuilder(BUILDER_START_CAPACITY);

		this.chunk = new byte[CHUNK_CAPACITY];
		this.chunkMaxSize = CHUNK_CAPACITY - (int) (this.file.length() % CHUNK_CAPACITY);
		this.chunkSize = 0;

		this.file.seek(this.file.length());
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return placeholder.getRequiredLogEntryValues();
	}

	@Override
	public void log(LogEntry entry) throws IOException {
		try {
			placeholder.render(builder, entry);
			storeStringBuilder();
		} finally {
			resetStringBuilder();
		}
	}

	@Override
	public void flush() throws IOException {
		if (chunkSize > 0) {
			file.write(chunk, 0, chunkSize);
			chunkMaxSize = CHUNK_CAPACITY == chunkSize ? CHUNK_CAPACITY : CHUNK_CAPACITY - chunkSize;
			chunkSize = 0;
		}
	}

	@Override
	public void close() throws IOException {
		try {
			flush();
		} finally {
			file.close();
		}
	}

	/**
	 * Stores the content of the string builder.
	 *
	 * @throws IOException Failed to encode the log entry or to write it to the target file
	 */
	private void storeStringBuilder() throws IOException {
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(builder));
		byte[] data = buffer.array();
		int dataLength = buffer.limit();

		int length = Math.min(dataLength, CHUNK_CAPACITY - chunkSize);
		System.arraycopy(data, 0, chunk, chunkSize, length);
		chunkSize += length;

		if (chunkSize >= chunkMaxSize) {
			flush();

			int index = length;
			length = ((buffer.limit() - index) / CHUNK_CAPACITY) * CHUNK_CAPACITY;
			file.write(data, index, length);

			index += length;
			length = dataLength - index;
			System.arraycopy(data, index, chunk, 0, length);
			chunkSize = length;
		}
	}

	/**
	 * Resets the string builder for writing the next log entry.
	 */
	private void resetStringBuilder() {
		if (builder.capacity() > BUILDER_MAX_CAPACITY) {
			builder = new StringBuilder(BUILDER_START_CAPACITY);
		} else {
			builder.setLength(0);
			builder.ensureCapacity(0);
		}
	}

}
