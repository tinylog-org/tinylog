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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.StringMatchers.matchesPattern;

import org.junit.Test;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for writing thread.
 *
 * @see WritingThread
 */
public class WritingThreadTest extends AbstractTest {

	/**
	 * Test simple startup and shutdown.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testSimpleStartupAndShutdown() throws InterruptedException {
		int threadCount = Thread.activeCount();

		WritingThread writingThread = new WritingThread(null, Thread.NORM_PRIORITY);
		assertEquals(threadCount, Thread.activeCount());

		writingThread.start();
		assertNull(writingThread.getNameOfThreadToObserve());
		assertTrue(writingThread.isAlive());
		assertEquals(threadCount + 1, Thread.activeCount());

		writingThread.shutdown();
		writingThread.join();
		assertFalse(writingThread.isAlive());
		assertEquals(threadCount, Thread.activeCount());
	}

	/**
	 * Test write log entries by writing thread.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testWritingLogEntries() throws InterruptedException {
		StoreWriter writer = new StoreWriter();
		WritingThread writingThread = new WritingThread(null, Thread.NORM_PRIORITY);
		writingThread.start();

		writingThread.putLogEntry(writer, new LogEntryBuilder().level(Level.INFO).message("sample").create());

		writingThread.shutdown();
		writingThread.join();

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("sample", logEntry.getMessage());
	}

	/**
	 * Test if writing thread flushes after an iteration.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testFlush() throws InterruptedException {
		DummyWriter writer1 = new DummyWriter();
		DummyWriter writer2 = new DummyWriter();

		WritingThread writingThread = new WritingThread(null, Thread.NORM_PRIORITY);
		writingThread.start();

		writingThread.putLogEntry(writer1, new LogEntryBuilder().message("one").create());
		writingThread.putLogEntry(writer2, new LogEntryBuilder().message("two").create());
		writingThread.putLogEntry(writer1, new LogEntryBuilder().message("three").create());
		writingThread.putLogEntry(writer1, new LogEntryBuilder().message("four").create());
		writingThread.putLogEntry(writer1, new LogEntryBuilder().message("five").create());

		writingThread.shutdown();
		writingThread.join();

		assertTrue(writer1.numberOfFlushes >= 1); // Should be flushed at least once
		assertTrue(writer1.numberOfFlushes < 4); // ... and ideally less than four times

		assertEquals(1, writer2.numberOfFlushes); // Should be flushed exactly once
	}

	/**
	 * Test exceptions for writing log entries by writing thread.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testFailedWritingLogEntries() throws InterruptedException {
		WritingThread writingThread = new WritingThread(null, Thread.NORM_PRIORITY);
		writingThread.start();

		writingThread.putLogEntry(new NullWriter() {

			@Override
			public void write(final LogEntry logEntry) {
				throw new UnsupportedOperationException();
			}

		}, new LogEntryBuilder().level(Level.INFO).message("sample").create());

		writingThread.shutdown();
		writingThread.join();

		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER ERROR\\: Failed to write log entry \\(.+\\)"));
	}

	/**
	 * Test exceptions for flushing writer.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testFailedFlushWriter() throws InterruptedException {
		WritingThread writingThread = new WritingThread(null, Thread.NORM_PRIORITY);
		writingThread.start();

		writingThread.putLogEntry(new NullWriter() {

			@Override
			public void flush() {
				throw new UnsupportedOperationException();
			}

		}, new LogEntryBuilder().level(Level.INFO).message("sample").create());

		writingThread.shutdown();
		writingThread.join();

		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER ERROR\\: Failed to flush writer \\(.+\\)"));
	}

	/**
	 * Test observing a thread and shutdown writing thread, when observed thread is dead.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testObservingThread() throws InterruptedException {
		EndlessThread observableThread = new EndlessThread();
		observableThread.setName(EndlessThread.class.getName());
		observableThread.setPriority(Thread.MIN_PRIORITY);
		observableThread.start();

		WritingThread writingThread = new WritingThread(EndlessThread.class.getName(), Thread.NORM_PRIORITY);
		writingThread.start();
		assertEquals(EndlessThread.class.getName(), writingThread.getNameOfThreadToObserve());
		assertEquals(observableThread, writingThread.getThreadToObserve());
		assertTrue(writingThread.isAlive());
		Thread.sleep(10L);
		assertTrue(writingThread.isAlive());

		observableThread.cancelled = true;
		writingThread.join();
		assertFalse(writingThread.isAlive());
	}

	/**
	 * Test observing a thread that doesn't exist.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testCannotFindThread() throws InterruptedException {
		WritingThread writingThread = new WritingThread(EndlessThread.class.getName(), Thread.NORM_PRIORITY);
		assertEquals(EndlessThread.class.getName(), writingThread.getNameOfThreadToObserve());
		assertNull(writingThread.getThreadToObserve());
	}

	private static final class EndlessThread extends Thread {

		private volatile boolean cancelled = false;

		@Override
		public void run() {
			while (!cancelled) {
				yield();
			}
		};

	}

	private static final class DummyWriter extends NullWriter {

		private int numberOfFlushes = 0;

		@Override
		public void flush() {
			++numberOfFlushes;
		}

	}

}
