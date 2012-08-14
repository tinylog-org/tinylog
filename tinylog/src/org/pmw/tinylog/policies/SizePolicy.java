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

package org.pmw.tinylog.policies;

import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.PropertiesLoader;

/**
 * Policy for limiting the size of log files.
 */
public class SizePolicy implements IPolicy {

	private final long maxSize;
	private long size;

	/**
	 * @param maxSize
	 *            Maximum size of a log file in bytes (must be > 0)
	 * @throws IllegalArgumentException
	 *             if maxSize is <= 0
	 */
	public SizePolicy(final long maxSize) throws IllegalArgumentException {
		if (maxSize <= 0L) {
			throw new IllegalArgumentException("maxSize must be > 0");
		}

		this.maxSize = maxSize;
		this.size = 0L;
	}

	/**
	 * String parameter for {@link PropertiesLoader}.
	 * 
	 * @param maxSize
	 *            Maximum size of a log file with byte unit (e.g 16MB)
	 * @throws IllegalArgumentException
	 *             if maxSize is <= 0
	 */
	SizePolicy(final String maxSize) throws IllegalArgumentException {
		try {
			if (maxSize.endsWith("GB")) {
				this.maxSize = Long.parseLong(maxSize.substring(0, maxSize.length() - 2).trim()) * 1024L * 1024L * 1024L;
			} else if (maxSize.endsWith("MB")) {
				this.maxSize = Long.parseLong(maxSize.substring(0, maxSize.length() - 2).trim()) * 1024L * 1024L;
			} else if (maxSize.endsWith("KB")) {
				this.maxSize = Long.parseLong(maxSize.substring(0, maxSize.length() - 2).trim()) * 1024L;
			} else {
				this.maxSize = Long.parseLong(maxSize);
			}
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Size is not a number");
		}
		if (this.maxSize <= 0L) {
			throw new IllegalArgumentException("maxSize must be > 0");
		}
		this.size = 0L;
	}

	@Override
	public final boolean check(final ELoggingLevel level, final String logEntry) {
		size += logEntry.getBytes().length;
		return size <= maxSize;
	}

	@Override
	public final void reset() {
		size = 0L;
	}

}
