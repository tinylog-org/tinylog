/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.jul;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Bridge for activating the redirection of log entries from {@code java.util.logging} to tinylog.
 */
public final class JulTinylogBridge {

	/** */
	private JulTinylogBridge() {
	}

	/**
	 * Activates the redirection.
	 * 
	 * <p>
	 * All existing {@link Handler handlers} will be removed. {@link Logger java.util.logging.Loggers} inherit the
	 * minimum activated severity level from tinylog.
	 * </p>
	 */
	public static void activate() {
		new BridgeHandler().activate();
	}

}
