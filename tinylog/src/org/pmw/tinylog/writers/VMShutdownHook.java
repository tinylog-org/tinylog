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

package org.pmw.tinylog.writers;

import java.util.ArrayList;
import java.util.Collection;

import org.pmw.tinylog.InternalLogger;

/**
 * Thread to shutdown logging writers at VM shutdown. The {@link org.pmw.tinylog.writers.LoggingWriter#close() close()}
 * method of all registered logging writers will be called.
 */
public final class VMShutdownHook extends Thread {

	private static final ShutdownThread shutdownThread = new ShutdownThread();
	private static final Collection<LoggingWriter> writers;
	private static boolean shutdown;

	static {
		writers = new ArrayList<LoggingWriter>();
		shutdown = false;
	}

	private VMShutdownHook() {
	}

	/**
	 * Register a logging writer, which should be shutdown automatically when VM will be shutdown.
	 * 
	 * @param writer
	 *            Logging writer to shutdown
	 */
	public static void register(final LoggingWriter writer) {
		synchronized (writers) {
			if (!shutdown) {
				if (writers.isEmpty()) {
					Runtime.getRuntime().addShutdownHook(shutdownThread);
				}
				writers.add(writer);
			}
		}
	}

	/**
	 * Remove a logging writer from the shutdown list.
	 * 
	 * @param writer
	 *            Logging writer to remove
	 */
	public static void unregister(final LoggingWriter writer) {
		synchronized (writers) {
			if (!shutdown) {
				writers.remove(writer);
				if (writers.isEmpty()) {
					Runtime.getRuntime().removeShutdownHook(shutdownThread);
				}
			}
		}
	}

	private static final class ShutdownThread extends Thread {

		@Override
		public void run() {
			synchronized (writers) {
				shutdown = true;

				for (LoggingWriter writer : writers) {
					try {
						writer.close();
					} catch (Exception ex) {
						InternalLogger.error(ex, "Failed to shutdown logging writer");
					}
				}

				writers.clear();
				shutdown = false;
			}
		}

	}

}
