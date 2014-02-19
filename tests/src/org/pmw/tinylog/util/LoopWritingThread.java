/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.tinylog.util;

import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.Writer;

/**
 * Writes the specified line in a loop to a given writer until shutdown command.
 */
public final class LoopWritingThread extends Thread {

	/**
	 * Line to write
	 */
	public static final String LINE = "!!! Hello World! !!! qwertzuiopasdfghjklyxcvbnm !!!";

	private final Writer writer;
	private long writtenLines;
	private volatile boolean shutdown;

	/**
	 * @param writer
	 *            Writer to output lines
	 */
	public LoopWritingThread(final Writer writer) {
		this.writer = writer;
		this.writtenLines = 0L;
		this.shutdown = false;
	}

	/**
	 * Get the number of written lines.
	 * 
	 * @return Number of written lines
	 */
	public long getWrittenLines() {
		return writtenLines;
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry(LINE + "\n").create());
				++writtenLines;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Shutdown thread and end writing.
	 */
	public void shutdown() {
		this.shutdown = true;
	}

}
