/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Writes log entries to a file.
 */
public class FileLoggingWriter implements ILoggingWriter {

	private final Lock lock;
	private final BufferedWriter writer;
	private boolean isClosed;

	/**
	 * @param file
	 *            The log file
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public FileLoggingWriter(final File file) throws IOException {
		super();
		this.lock = new ReentrantLock();
		this.writer = new BufferedWriter(new FileWriter(file));
		this.isClosed = false;
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		lock.lock();
		try {
			if (!isClosed) {
				writer.write(logEntry);
				writer.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public final void close() throws IOException {
		lock.lock();
		try {
			if (!isClosed) {
				isClosed = true;
				writer.close();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected final void finalize() throws Throwable {
		close();
	}
}
