/*
 * Copyright 2017 Martin Winandy
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.provider.InternalLogger;
import org.tinylog.writers.raw.ByteArrayWriter;

/**
 * Writer for outputting log entries to a shared log file. Multiple processes can write simultaneously to the same log
 * file. The shared file writer ensures that no process overrides log entries of another process. Already existing files
 * can be continued. The output can be buffered for improving performance, but is not recommended as outputs of
 * different processes will be not in chronological order.
 */
public final class SharedFileWriter extends AbstractFormatPatternWriter {

	private final RandomAccessFile lockFile;
	private final FileLock lock;

	private final Charset charset;
	private final ByteArrayWriter writer;

	/**
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IOException
	 *             Lock for log file cannot be accessed
	 * @throws IllegalArgumentException
	 *             Log file is not defined in configuration
	 */
	public SharedFileWriter() throws FileNotFoundException, IOException {
		this(Collections.<String, String>emptyMap());
	}

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IOException
	 *             Lock for log file cannot be accessed
	 * @throws IllegalArgumentException
	 *             Log file is not defined in configuration
	 */
	public SharedFileWriter(final Map<String, String> properties) throws FileNotFoundException, IOException {
		super(properties);

		String fileName = getFileName(properties);
		boolean append = Boolean.parseBoolean(properties.get("append"));
		boolean buffered = Boolean.parseBoolean(properties.get("buffered"));
		boolean writingThread = Boolean.parseBoolean(properties.get("writingthread"));

		if (append) {
			lockFile = null;
			lock = null;
		} else {
			lockFile = new RandomAccessFile(fileName + ".lock", "rw");
			FileLock exclusiveLock = lockFile.getChannel().tryLock(0, Long.MAX_VALUE, false);
			if (exclusiveLock == null) {
				append = true;
			} else {
				exclusiveLock.release();
			}
	
			lock = lockFile.getChannel().lock(0, Long.MAX_VALUE, true);
			if (!lock.isShared()) {
				append = true;
				lock.release();
				InternalLogger.log(Level.WARN, "Operating system does not support shared locks. "
						+ "Shared file writer will only work properly, if append mode is enabled.");
			}
		}

		charset = getCharset(properties);
		writer = createByteArrayWriter(fileName, append, buffered, !writingThread, true);
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		byte[] data = render(logEntry).getBytes(charset);
		writer.write(data, 0, data.length);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			writer.close();
		} finally {
			if (lockFile != null) {
				try {
					lock.release();
				} finally {
					lockFile.close();
				}
			}
		}
	}

}
