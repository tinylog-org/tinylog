/*
 * Copyright 2016 Martin Winandy
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
import java.nio.charset.Charset;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.writers.raw.ByteArrayWriter;

/**
 * Writer for outputting log entries to a log file. Already existing files can be continued and the output can be
 * buffered for improving performance.
 */
public final class FileWriter extends AbstractFormatPatternWriter {

	private final Charset charset;
	private final ByteArrayWriter writer;

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IllegalArgumentException
	 *             Log file is not defined in configuration
	 */
	public FileWriter(final Map<String, String> properties) throws FileNotFoundException {
		super(properties);

		String fileName = getFileName(properties);
		boolean append = Boolean.parseBoolean(properties.get("append"));
		boolean buffered = Boolean.parseBoolean(properties.get("buffered"));
		boolean writingThread = Boolean.parseBoolean(properties.get("writingthread"));

		charset = getCharset(properties);
		writer = createByteArrayWriter(fileName, append, buffered, !writingThread);
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		byte[] data = render(logEntry).getBytes(charset);
		writer.write(data, data.length);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
