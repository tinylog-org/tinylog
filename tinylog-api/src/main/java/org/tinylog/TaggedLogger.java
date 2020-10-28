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

package org.tinylog;

import org.tinylog.core.Configuration;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LoggingBackend;

/**
 * Logger for issuing tagged log entries.
 */
public final class TaggedLogger {

	private final String tag;
	private final Configuration configuration;
	private final LoggingBackend backend;

	/**
	 * @param tag Case-sensitive tag for the logger
	 */
	TaggedLogger(String tag) {
		this(tag, Tinylog.getConfiguration(), Tinylog.getLoggingBackend());
	}

	/**
	 * @param tag Case-sensitive tag for the logger
	 * @param configuration Custom configuration for the logger
	 * @param backend Logging backend for the logger
	 */
	TaggedLogger(String tag, Configuration configuration, LoggingBackend backend) {
		this.tag = tag;
		this.configuration = configuration;
		this.backend = backend;
	}

	/**
	 * Gets the assigned case-sensitive tag.
	 *
	 * @return The assigned tag or {@code null} if the logger is untagged
	 */
	public String getTag() {
		return tag;
	}

}
