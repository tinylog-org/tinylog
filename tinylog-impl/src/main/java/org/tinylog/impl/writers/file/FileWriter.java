package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.writers.AsyncWriter;

/**
 * Asynchronous writer for writing log entries to a file.
 */
public class FileWriter implements AsyncWriter {

	private static final int BYTE_BUFFER_CAPACITY = 64 * 1024; // 64 KB
	private static final int BUILDER_START_CAPACITY = 1024;    //  1 KB
	private static final int BUILDER_MAX_CAPACITY = 64 * 1024; // 64 KB

	private final Charset charset;
	private final OutputFormat format;
	private final LogFile file;
	private final StringBuilder builder;

	/**
	 * @param format The output format for log entries
	 * @param file The path to the target log file
	 * @param charset The charset to use for writing strings to the target file
	 * @throws IOException Failed to access the target log file
	 */
	public FileWriter(OutputFormat format, Path file, Charset charset) throws IOException {
		Path parent = file.toAbsolutePath().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		this.charset = charset;
		this.format = format;
		this.file = new LogFile(file.toString(), BYTE_BUFFER_CAPACITY, charset, true);
		this.builder = new StringBuilder(BUILDER_START_CAPACITY);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return format.getRequiredLogEntryValues();
	}

	@Override
	public void log(LogEntry entry) throws IOException {
		try {
			format.render(builder, entry);
			String content = builder.toString();
			byte[] data = content.getBytes(charset);
			file.write(data);
		} finally {
			resetStringBuilder();
		}
	}

	@Override
	public void flush() throws IOException {
		file.flush();
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	/**
	 * Resets the string builder for writing the next log entry.
	 */
	private void resetStringBuilder() {
		if (builder.capacity() > BUILDER_MAX_CAPACITY) {
			builder.setLength(BUILDER_MAX_CAPACITY);
			builder.trimToSize();
		}

		builder.setLength(0);
	}

}
