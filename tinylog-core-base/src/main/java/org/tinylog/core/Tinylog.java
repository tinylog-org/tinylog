/*
 * Copyright 2020 Martin Winandy
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

import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.RuntimeProvider;

/**
 * Global access to the tinylog framework.
 */
public final class Tinylog {

	private static final RuntimeFlavor runtime = new RuntimeProvider().getRuntime();
	private static final Framework framework = new Framework();

	/** */
	private Tinylog() {
	}

	/**
	 * Provides the appropriate {@link RuntimeFlavor} for the actual virtual machine.
	 *
	 * @return The appropriate runtime instance
	 */
	public static RuntimeFlavor getRuntime() {
		return runtime;
	}

	/**
	 * Gets the configuration.
	 *
	 * @return The configuration of tinylog
	 */
	public static Configuration getConfiguration() {
		return framework.getConfiguration();
	}

	/**
	 * Gets the logging provider implementation of the actual logging back-end.
	 *
	 * <p>
	 *     This method should only be called by (third-party) logging APIs. Logging providers are not to be used for
	 *     logging in an application or library.
	 * </p>
	 *
	 * @return The logging provider implementation of the actual logging back-end
	 */
	public static LoggingProvider getLoggingProvider() {
		return framework.getLoggingProvider();
	}

	/**
	 * Registers a new {@link Hook}.
	 *
	 * @param hook Hook to register
	 */
	public void registerHook(Hook hook) {
		framework.registerHook(hook);
	}

	/**
	 * Removes a registered {@link Hook}.
	 *
	 * @param hook Hook to unregister
	 */
	public void removeHook(Hook hook) {
		framework.removeHook(hook);
	}

	/**
	 * Starts tinylog if it is not running yet.
	 *
	 * <p>
	 *     When the first log entry is issued, tinylog starts automatically by itself. This method only needs to be
	 *     called in a few corner cases.
	 * </p>
	 */
	public static void startUp() {
		framework.startUp();
	}

	/**
	 * Shuts down tinylog manually.
	 *
	 * <p>
	 *     If auto shutdown has not been disabled, tinylog will shut down by itself. This method only needs to be called
	 *     if auto shutdown has been explicitly disabled.
	 * </p>
	 */
	public static void shutDown() {
		framework.shutDown();
	}

}
