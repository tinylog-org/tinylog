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

package org.tinylog.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;
import org.tinylog.writers.Writer;

/**
 * Thread for writing log entries asynchronously.
 */
public final class WritingThread extends Thread {

	private static final String THREAD_NAME = "tinylog-WritingThread";
	private static final long MILLISECONDS_TO_SLEEP = 10L;

	private final Object mutex;
	private final Collection<Writer> writers;
	private List<Task> tasks;

	/**
	 * @param writers
	 *            Open writers
	 */
	WritingThread(final Collection<Writer> writers) {
		this.mutex = new Object();
		this.writers = writers;
		this.tasks = new ArrayList<Task>();

		setName(THREAD_NAME);
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}

	/**
	 * Fetches log entries and writes them until receiving a poison task.
	 */
	@Override
	public void run() {
		Collection<Writer> writers = new ArrayList<Writer>(1);

		while (true) {
			for (Task task : receiveTasks()) {
				if (task == Task.POISON) {
					close();
					return;
				} else {
					write(writers, task);
				}
			}

			flush(writers);
			writers.clear();

			try {
				sleep(MILLISECONDS_TO_SLEEP);
			} catch (InterruptedException ex) {
				// Ignore and continue
			}
		}
	}

	/**
	 * Adds a log entry for writing.
	 *
	 * @param writer
	 *            Writer to write given log entry
	 * @param logEntry
	 *            Log entry to write
	 */
	public void add(final Writer writer, final LogEntry logEntry) {
		Task task = new Task(writer, logEntry);
		synchronized (mutex) {
			tasks.add(task);
		}
	}

	/**
	 * Shuts this writing thread down after writing all already added log entries.
	 *
	 * <p>
	 * This method doesn't wait until the writing thread is down. {@link Thread#join()} can be used afterwards to wait
	 * for termination.
	 * </p>
	 */
	public void shutdown() {
		synchronized (mutex) {
			tasks.add(Task.POISON);
		}

		interrupt();
	}

	/**
	 * Receives all added log entries.
	 *
	 * @return Log entries to write
	 */
	private List<Task> receiveTasks() {
		synchronized (mutex) {
			if (tasks.isEmpty()) {
				return Collections.emptyList();
			} else {
				List<Task> currentTasks = tasks;
				tasks = new ArrayList<Task>();
				return currentTasks;
			}
		}
	}

	/**
	 * Writes a log entry.
	 *
	 * @param writers
	 *            Mutable collection of used writers
	 * @param task
	 *            Log entry to write
	 */
	private void write(final Collection<Writer> writers, final Task task) {
		try {
			Writer writer = task.writer;
			writer.write(task.logEntry);
			if (!writers.contains(writer)) {
				writers.add(writer);
			}
		} catch (Exception ex) {
			InternalLogger.log(Level.ERROR, ex, "Failed to write log entry '" + task.logEntry.getMessage() + "'");
		}
	}

	/**
	 * Flushes a collection of writers.
	 *
	 * @param writers
	 *            Writers to flush
	 */
	private void flush(final Collection<Writer> writers) {
		for (Writer writer : writers) {
			try {
				writer.flush();
			} catch (Exception ex) {
				InternalLogger.log(Level.ERROR, ex, "Failed to flush writer");
			}
		}
	}

	/**
	 * Closes all writers.
	 */
	private void close() {
		for (Writer writer : writers) {
			try {
				writer.close();
			} catch (Exception ex) {
				InternalLogger.log(Level.ERROR, ex, "Failed to close writer");
			}
		}
	}

	/**
	 * Tuple that contains a log entry and a writer for outputting it.
	 */
	private static final class Task {

		private static final Task POISON = null;

		private final Writer writer;
		private final LogEntry logEntry;

		/**
		 * @param writer
		 *            Writer for outputting log entry
		 * @param logEntry
		 *            Log entry to write
		 */
		Task(final Writer writer, final LogEntry logEntry) {
			this.writer = writer;
			this.logEntry = logEntry;
		}

	}

}
