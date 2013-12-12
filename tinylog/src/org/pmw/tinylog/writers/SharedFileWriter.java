/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.writers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.LoggingLevel;

/**
 * Writes log entries to a shared file.
 * 
 * Multiple instances of a program are allowed to log into the same file.
 */
@PropertiesSupport(name = "sharedfile", properties = @Property(name = "filename", type = String.class))
public final class SharedFileWriter implements LoggingWriter {

	private static final Map<String, Mutex> mutexes = new HashMap<String, Mutex>();

	private final File file;
	private final Mutex internalMutex;
	private FileOutputStream stream;
	private java.io.OutputStreamWriter writer;

	/**
	 * @param filename
	 *            Filename of the log file
	 */
	public SharedFileWriter(final String filename) {
		this.file = new File(filename);

		String key = file.getAbsolutePath();
		Mutex mutex;
		synchronized (mutexes) {
			mutex = mutexes.get(key);
			if (mutex == null) {
				mutex = new Mutex(1);
				mutexes.put(key, mutex);
			} else {
				++mutex.count;
			}
		}
		this.internalMutex = mutex;
	}

	/**
	 * Get the filename of the log file.
	 * 
	 * @return Filename of the log file
	 */
	public String getFilename() {
		return file.getAbsolutePath();
	}

	@Override
	public void init() {
		try {
			if (file.isFile()) {
				file.delete();
			}
			stream = new FileOutputStream(file, true);
			writer = new OutputStreamWriter(stream);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		try {
			FileChannel channel = stream.getChannel();
			synchronized (internalMutex) {
				FileLock externalLock = channel.lock();
				try {
					writer.write(logEntry);
					writer.flush();
				} finally {
					externalLock.release();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public void close() throws IOException {
		synchronized (mutexes) {
			if (internalMutex.count > 1) {
				--internalMutex.count;
			} else {
				String key = file.getAbsolutePath();
				mutexes.remove(key);
			}
		}

		writer.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private static final class Mutex {

		private int count;

		public Mutex(final int count) {
			this.count = count;
		}

	}

}
