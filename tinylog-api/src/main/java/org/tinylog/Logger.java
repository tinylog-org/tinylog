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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final ConcurrentMap<String, TaggedLogger> loggers = new ConcurrentHashMap<String, TaggedLogger>();
	private static final TaggedLogger logger = new TaggedLogger(null);

	/** */
	private Logger() {
	}

	/**
	 * Retrieves a tagged logger instance. Tags are case-sensitive. If a tagged logger does not yet exists for the
	 * passed tag, a new logger is created. This method always returns the same logger instance for the same tag.
	 *
	 * @param tag Tag for logger or {@code null} for receiving an untagged logger
	 * @return Logger instance
	 */
	public static TaggedLogger tag(String tag) {
		if (tag == null || tag.isEmpty()) {
			return logger;
		} else {
			TaggedLogger logger = loggers.get(tag);
			if (logger == null) {
				logger = new TaggedLogger(tag);
				TaggedLogger existing = loggers.putIfAbsent(tag, logger);
				return existing == null ? logger : existing;
			} else {
				return logger;
			}
		}
	}

}
