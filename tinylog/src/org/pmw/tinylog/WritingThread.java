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

import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Thread to write log entries asynchronously.
 */
final class WritingThread extends Thread {

	private static final String THREAD_NAME = "tinylog-WritingThread";

	private volatile List<WritingTask> entries;
	private final String nameOfThreadToObserve;
	private final Thread threadToObserve;
	private volatile boolean shutdown;

	/**
	 * This thread will automatically shutdown, if the observed thread is dead.
	 * 
	 * @param nameOfThreadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @param priority
	 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY})
	 */
	WritingThread(final String nameOfThreadToObserve, final int priority) {
		this.entries = new ArrayList<WritingTask>();
		this.nameOfThreadToObserve = nameOfThreadToObserve;
		this.threadToObserve = nameOfThreadToObserve == null ? null : getThread(nameOfThreadToObserve);

		setName(THREAD_NAME);
		setPriority(priority);
	}

	/**
	 * Get the name of the thread, which is observed by this writhing thread.
	 * 
	 * @return Name of the thread
	 */
	public String getNameOfThreadToObserve() {
		return nameOfThreadToObserve;
	}

	/**
	 * Get the observed thread.
	 * 
	 * @return Observed thread
	 */
	public Thread getThreadToObserve() {
		return threadToObserve;
	}

	/**
	 * Put a log entry to write.
	 * 
	 * @param writer
	 *            Writer to write the log entry
	 * @param logEntry
	 *            Log entry to write
	 */
	public synchronized void putLogEntry(final LoggingWriter writer, final LogEntry logEntry) {
		entries.add(new WritingTask(writer, logEntry));
	}

	@Override
	public void run() {
		while (true) {
			boolean doShutdown = shutdown || (threadToObserve != null && !threadToObserve.isAlive());

			List<WritingTask> writingTasks = getWritingTasks();
			while (writingTasks != null) {
				for (WritingTask writingTask : writingTasks) {
					try {
						writingTask.writer.write(writingTask.logEntry);
					} catch (Exception ex) {
						InternalLogger.error(ex, "Failed to write log entry");
					}
				}
				writingTasks = getWritingTasks();
			}

			if (doShutdown) {
				break;
			} else {
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

		Thread[] threads;
		int size;
		int count;
		do {
			size = Math.max(32, root.activeCount() * 2);
			threads = new Thread[size];
			count = root.enumerate(threads);
		} while (count >= size);

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

	private synchronized List<WritingTask> getWritingTasks() {
		if (entries.isEmpty()) {
			return null;
		} else {
			List<WritingTask> entriesToWrite = entries;
			entries = new ArrayList<WritingTask>();
			return entriesToWrite;
		}
	}

	private static final class WritingTask {

		private final LoggingWriter writer;
		private final LogEntry logEntry;

		public WritingTask(final LoggingWriter writer, final LogEntry logEntry) {
			this.writer = writer;
			this.logEntry = logEntry;
		}

	}

}
