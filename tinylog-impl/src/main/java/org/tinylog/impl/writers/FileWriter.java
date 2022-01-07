package org.tinylog.impl.writers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
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
	private final Charset charset;

	private final ByteChunk chunk;
	private StringBuilder builder;

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
		this.charset = charset;

		this.chunk = new ByteChunk(CHUNK_CAPACITY, (int) (this.file.length() % CHUNK_CAPACITY));
		this.builder = new StringBuilder(BUILDER_START_CAPACITY);

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
		if (!chunk.isEmpty()) {
			int bytes = chunk.writeTo(file);
			chunk.reset(bytes == CHUNK_CAPACITY ? CHUNK_CAPACITY : CHUNK_CAPACITY - bytes);
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
		byte[] data = builder.toString().getBytes(charset);
		int bytes = chunk.store(data, 0);

		if (chunk.isFull()) {
			chunk.writeTo(file);
			chunk.reset(CHUNK_CAPACITY);

			int remainingChunks = (data.length - bytes) / CHUNK_CAPACITY;
			if (remainingChunks > 0) {
				int length = remainingChunks * CHUNK_CAPACITY;
				file.write(data, bytes, length);
				bytes += length;
			}

			if (bytes < data.length) {
				chunk.store(data, bytes);
			}
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
