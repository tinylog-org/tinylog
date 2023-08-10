/*
 * Copyright 2021 Martin Winandy
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;
import org.tinylog.writers.raw.BufferedWriterDecorator;
import org.tinylog.writers.raw.ByteArrayWriter;
import org.tinylog.writers.raw.CharsetAdjustmentWriterDecorator;
import org.tinylog.writers.raw.LockedRandomAccessFileWriter;
import org.tinylog.writers.raw.RandomAccessFileWriter;
import org.tinylog.writers.raw.SynchronizedWriterDecorator;

/**
 * Base writer for outputting log entries into files.
 */
public abstract class AbstractFileBasedWriter extends AbstractWriter {

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	protected AbstractFileBasedWriter(final Map<String, String> properties) {
		super(properties);
	}

	/**
	 * Extracts the log file name from configuration.
	 *
	 * @return Log file name
	 * @throws IllegalArgumentException
	 *             Log file is not defined in configuration
	 */
	protected String getFileName() {
		String fileName = getStringValue("file");
		if (fileName == null) {
			throw new IllegalArgumentException("File name is missing for writer");
		} else {
			return fileName;
		}
	}

	/**
	 * Extracts the charset from configuration. The default charset will be returned, if no charset is defined or the
	 * defined charset doesn't exist.
	 *
	 * @return Configured charset
	 */
	protected Charset getCharset() {
		String charsetName = getStringValue("charset");
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
	 * @param shared
	 *            Output file is shared with other processes
	 * @param charset
	 *            Charset used by the writer
	 * @return Writer for writing to passed file
	 * @throws IOException
	 *             Log file cannot be opened for write access
	 */
	protected static ByteArrayWriter createByteArrayWriter(final String fileName, final boolean append,
			final boolean buffered, final boolean threadSafe, final boolean shared, final Charset charset)
			throws IOException {
		File file = new File(fileName).getAbsoluteFile();
		file.getParentFile().mkdirs();

		byte[] charsetHeader = getCharsetHeader(charset);
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

		ByteArrayWriter writer;
		if (shared) {
			FileLock lock = randomAccessFile.getChannel().lock();
			try {
				prepareLogFile(randomAccessFile, append, charsetHeader);
			} finally {
				lock.release();
			}

			writer = new LockedRandomAccessFileWriter(randomAccessFile);
		} else {
			prepareLogFile(randomAccessFile, append, charsetHeader);
			writer = new RandomAccessFileWriter(randomAccessFile);
		}

		if (buffered) {
			writer = new BufferedWriterDecorator(writer);
		}

		if (threadSafe) {
			writer = new SynchronizedWriterDecorator(writer, randomAccessFile);
		}

		if (charsetHeader.length > 0) {
			writer = new CharsetAdjustmentWriterDecorator(writer, charsetHeader);
		}

		return writer;
	}

	/**
	 * Generate the header for the passed charset (for example BOM for UTF-16).
	 *
	 * @param charset
	 *            Charset for which the header should be generated for
	 * @return Generated charset header (can be empty depending on the passed charset)
	 */
	protected static byte[] getCharsetHeader(final Charset charset) {
		// Unfortunately, Java does not create charset headers for empty strings. Therefore, this method uses a
		// workaround with comparing a single space with a double space.
		byte[] singleSpace = " ".getBytes(charset);
		byte[] doubleSpace = "  ".getBytes(charset);
		return Arrays.copyOf(doubleSpace, singleSpace.length * 2 - doubleSpace.length);
	}

	private static void prepareLogFile(final RandomAccessFile randomAccessFile, final boolean append,
			final byte[] charsetHeader) throws IOException {
		if (append) {
			randomAccessFile.seek(randomAccessFile.length());
		} else {
			randomAccessFile.setLength(0);
		}

		if (charsetHeader.length > 0 && randomAccessFile.length() == 0) {
			randomAccessFile.write(charsetHeader, 0, charsetHeader.length);
		}
	}

}
