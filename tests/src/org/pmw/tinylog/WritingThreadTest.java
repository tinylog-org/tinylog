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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for writing thread.
 * 
 * @see WritingThread
 */
public class WritingThreadTest {

	/**
	 * Test simple startup and shutdown.
	 * 
	 * @throws InterruptedException
	 *             Thread was interrupted
	 */
	@Test
	public final void testSimpleStartupAndShutdown() throws InterruptedException {
		int threadCount = Thread.activeCount();

		WritingThread writingThread = new WritingThread(null);
		assertEquals(threadCount, Thread.activeCount());

		writingThread.start();
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
	 *             Thread was interrupted
	 */
	@Test
	public final void testWritingLogEntries() throws InterruptedException {
		StoreWriter writer = new StoreWriter();
		WritingThread writingThread = new WritingThread(null);
		writingThread.start();

		writingThread.putLogEntry(writer, LoggingLevel.INFO, "sample");

		writingThread.shutdown();
		writingThread.join();

		assertEquals("sample", writer.consumeMessage());
	}

	/**
	 * Test observing a thread and shutdown writing thread, when observed thread is dead.
	 * 
	 * @throws InterruptedException
	 *             Thread was interrupted
	 */
	@Test
	public final void testObservingThread() throws InterruptedException {
		EndlessThread observableThread = new EndlessThread();
		observableThread.setName(EndlessThread.class.getName());
		observableThread.setPriority(Thread.MIN_PRIORITY);
		observableThread.start();

		WritingThread writingThread = new WritingThread(EndlessThread.class.getName());
		writingThread.start();
		assertTrue(writingThread.isAlive());
		Thread.sleep(100L);
		assertTrue(writingThread.isAlive());

		observableThread.cancelled = true;
		Thread.sleep(100L);
		assertFalse(writingThread.isAlive());
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

}
