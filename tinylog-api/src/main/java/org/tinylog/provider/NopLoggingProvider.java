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

package org.tinylog.provider;

import org.tinylog.Level;

/**
 * Logging provider implementation that does nothing. All log entries will be ignored.
 */
final class NopLoggingProvider implements LoggingProvider {

	private static final ContextProvider contextProvider = new NopContextProvider();

	/** */
	NopLoggingProvider() {
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	@Override
	public Level getMinimumLevel(final String tag) {
		return Level.OFF;
	}

	@Override
	public boolean isEnabled(final int depth, final String tag, final Level level) {
		return false;
	}

	@Override
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final Object obj,
		final Object... arguments) {
		// Ignore
	}

	@Override
	public void shutdown() {
		// Ignore
	}

}
