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

package org.pmw.tinylog.mocks;

import mockit.Mock;
import mockit.MockUp;

/**
 * Mock for Thread to handle sleeps.
 * 
 * @see Thread
 */
public final class SleepHandledThreadMock extends MockUp<Thread> {

	private static volatile boolean enabled;
	private static volatile boolean sleeping;

	/** */
	public SleepHandledThreadMock() {
		enabled = true;
		sleeping = false;
	}

	/**
	 * Enable this mock ({@link Thread#sleep(long)} will be mocked).
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Disable this mock ({@link Thread#sleep(long)} won't be mocked).
	 */
	public void disable() {
		enabled = false;
		awake();
	}

	/**
	 * Check if observed thread is sleeping.
	 * 
	 * @return <code>true</code> if thread is sleeping, <code>false</code> if not
	 */
	public boolean isSleeping() {
		return sleeping;
	}

	/**
	 * Block the current thread until the observed thread starts to sleep.
	 */
	public void waitForSleep() {
		while (!sleeping) {
			Thread.yield();
		}
	}

	/**
	 * Awake the observed thread.
	 */
	public void awake() {
		sleeping = false;
	}

	/**
	 * Mocked method {@link Thread#sleep(long)}.
	 * 
	 * @param millis
	 *            Length of time to sleep in milliseconds (will be ignored)
	 * @throws InterruptedException
	 *             Thread was interrupted
	 * 
	 * @see Thread#sleep(long)
	 */
	@Mock
	public static void sleep(final long millis) throws InterruptedException {
		if (enabled) {
			sleeping = true;
			while (sleeping) {
				Thread.yield();
			}
		} else {
			long startTime = System.currentTimeMillis();
			while (startTime + millis < System.currentTimeMillis()) {
				Thread.yield();
			}
		}
	}

}
