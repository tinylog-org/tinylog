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

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Thread to write log entries.
 */
final class WritingThread extends Thread {

	/**
	 * Name of default thread to observe
	 */
	static final String DEFAULT_THREAD_TO_OBSERVE = "main";

	/**
	 * Default priority for writhing thread
	 */
	static final int DEFAULT_PRIORITY = 2;

	private volatile List<LogEntry> entries;
	private final Thread threadToObserve;
	private volatile boolean shutdown;

	/**
	 * This thread will automatically shutdown, if the observed thread is dead.
	 * 
	 * @param nameOfThreadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 */
	public WritingThread(final String nameOfThreadToObserve) {
		entries = new ArrayList<WritingThread.LogEntry>();
		threadToObserve = nameOfThreadToObserve == null ? null : getThread(nameOfThreadToObserve);
	}

	/**
	 * Put a log entry to write.
	 * 
	 * @param writer
	 *            Writer to write this log entry
	 * @param level
	 *            Level of the log entry
	 * @param text
	 *            Formatted log entry to write
	 */
	public synchronized void putLogEntry(final LoggingWriter writer, final LoggingLevel level, final String text) {
		entries.add(new LogEntry(writer, level, text));
	}

	@Override
	public void run() {
		while (true) {
			boolean doShutdown = shutdown;

			if (threadToObserve != null) {
				doShutdown |= !threadToObserve.isAlive();
			}

			List<LogEntry> entriesToWrite = getLogEntriesToWrite();
			while (entriesToWrite != null) {
				for (LogEntry entry : entriesToWrite) {
					entry.writer.write(entry.level, entry.text);
				}
				entriesToWrite = getLogEntriesToWrite();
			}

			if (doShutdown) {
				break;
			}

			if (!shutdown) {
				try {
					sleep(10L);
				} catch (InterruptedException ex) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Shutdown thread.
	 */
	public void shutdown() {
		shutdown = true;
		interrupt();
	}

	private static Thread getThread(final String name) {
		ThreadGroup root = getRootThreadGroup(Thread.currentThread().getThreadGroup());

		Thread[] threads = new Thread[root.activeCount()];
		int count = root.enumerate(threads);

		for (int i = 0; i < count; ++i) {
			if (name.equals(threads[i].getName())) {
				return threads[i];
			}
		}

		return null;
	}

	private static ThreadGroup getRootThreadGroup(final ThreadGroup threadGroup) {
		ThreadGroup parent = threadGroup.getParent();
		if (parent == null) {
			return threadGroup;
		}
		return getRootThreadGroup(parent);
	}

	private synchronized List<LogEntry> getLogEntriesToWrite() {
		if (entries.isEmpty()) {
			return null;
		} else {
			List<LogEntry> entriesToWrite = entries;
			entries = new ArrayList<WritingThread.LogEntry>();
			return entriesToWrite;
		}
	}

	private static final class LogEntry {

		private final LoggingWriter writer;
		private final LoggingLevel level;
		private final String text;

		public LogEntry(final LoggingWriter writer, final LoggingLevel level, final String text) {
			this.writer = writer;
			this.level = level;
			this.text = text;
		}

	}

}
