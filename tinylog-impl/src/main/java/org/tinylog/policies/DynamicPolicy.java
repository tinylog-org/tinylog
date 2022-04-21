/*
 * Copyright 2021 Martin Winandy
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
import org.tinylog.path.DynamicSegment;
import org.tinylog.provider.InternalLogger;

/**
 * Policy for triggering a manual rollover by calling {@link #setReset()}.
 * Might be used together with {@link DynamicSegment}.
 */
public final class DynamicPolicy implements Policy {

	private static volatile boolean reset;

	/** */
	public DynamicPolicy() {
		this(null);
	}

	/**
	 * @param argument Should be always {@code null} as dynamic policy does not support arguments
	 */
	public DynamicPolicy(final String argument) {
		if (argument != null) {
			InternalLogger.log(Level.WARN, "Dynamic policy does not support arguments");
		}
	}

	@Override
	public boolean continueExistingFile(final String path) {
		return true;
	}

	@Override
	public boolean continueCurrentFile(final byte[] entry) {
		return !reset;
	}

	@Override
	public void reset() {
		reset = false;
	}

	/**
	 * Sets the reset flag to trigger a {@linkplain #reset() reset}.
	 */
	public static void setReset() {
		reset = true;
	}

}
