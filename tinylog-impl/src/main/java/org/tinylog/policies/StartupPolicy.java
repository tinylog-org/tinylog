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

package org.tinylog.policies;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Policy for triggering a rollover at startup.
 */
public final class StartupPolicy implements Policy {

	/** */
	public StartupPolicy() {
		this(null);
	}

	/**
	 * @param argument
	 *            Should be always {@code null} as startup policy does not support arguments
	 */
	public StartupPolicy(final String argument) {
		if (argument != null) {
			InternalLogger.log(Level.WARN, "Startup policy does not support arguments");
		}
	}

	@Override
	public boolean continueExistingFile(final String path) {
		return false;
	}

	@Override
	public boolean continueCurrentFile(final byte[] entry) {
		return true;
	}

	@Override
	public void reset() {
	}

}
