package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.path.DynamicPath;
import org.tinylog.impl.policies.Policy;
import org.tinylog.impl.writers.AsyncWriter;

/**
 * Asynchronous writer for writing log entries to a file.
 */
public class FileWriter implements AsyncWriter {

	private static final int BYTE_BUFFER_CAPACITY = 64 * 1024; // 64 KB
	private static final int BUILDER_START_CAPACITY = 1024;    //  1 KB
	private static final int BUILDER_MAX_CAPACITY = 64 * 1024; // 64 KB

	private final DynamicPath path;
	private final Charset charset;
	private final byte[] bom;
	private final OutputFormat format;
	private final Policy policy;
	private final StringBuilder builder;

	private LogFile logFile;

	/**
	 * @param format The output format for log entries
	 * @param policy The policy for starting new log files
	 * @param path The dynamic path to the target log file
	 * @param charset The charset to use for writing strings to the target file
	 * @throws Exception Failed to access the target log file
	 */
	public FileWriter(OutputFormat format, Policy policy, DynamicPath path, Charset charset) throws Exception {
		final Path actualPath = path.generateNewPath();

		this.path = path;
		this.charset = charset;
		this.bom = createBom(charset);
		this.format = format;
		this.policy = policy;
		this.builder = new StringBuilder(BUILDER_START_CAPACITY);
		this.logFile = createLogFile(actualPath, bom, policy.canContinueFile(actualPath), policy);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return format.getRequiredLogEntryValues();
	}

	@Override
	public void log(LogEntry entry) throws Exception {
		try {
			format.render(builder, entry);
			String content = builder.toString();
			byte[] data = content.getBytes(charset);

			if (!policy.canAcceptLogEntry(data.length - bom.length)) {
				close();
				logFile = createLogFile(path.generateNewPath(), bom, false, policy);
			}

			logFile.write(data, bom.length);
		} finally {
			resetStringBuilder();
		}
	}

	@Override
	public void flush() throws IOException {
		logFile.flush();
	}

	@Override
	public void close() throws IOException {
		logFile.close();
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

	/**
	 * Creates the BOM for a charset.
	 *
	 * @param charset The charset for which the BOM should be created
	 * @return The BOM or an empty byte array if the passed charset does not have a BOM
	 */
	private static byte[] createBom(Charset charset) {
		byte[] singleSpace = " ".getBytes(charset);
		byte[] doubleSpaces = "  ".getBytes(charset);
		return Arrays.copyOf(doubleSpaces, singleSpace.length * 2 - doubleSpaces.length);
	}

	/**
	 * Creates a new log file.
	 *
	 * @param file The full path to the log file
	 * @param bom The BOM for the start of the log file
	 * @param append {@code true} for appending an already existing file, {@code false} for overwriting an already
	 *               existing file
	 * @param policy The policy to initialize with the new log file
	 * @return The created and opened log file
	 * @throws Exception Failed to create the log file or to initialize the policy
	 */
	private static LogFile createLogFile(Path file, byte[] bom, boolean append, Policy policy) throws Exception {
		LogFile logFile = new LogFile(file, BYTE_BUFFER_CAPACITY, append);
		policy.init(file);

		if (logFile.isNewFile() && bom.length > 0) {
			policy.canAcceptLogEntry(bom.length);
			logFile.write(bom, 0);
		}

		return logFile;
	}

}
