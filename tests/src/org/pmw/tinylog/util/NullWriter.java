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

package org.pmw.tinylog.util;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * This writer does nothing and just ignores all log entries.
 */
public final class NullWriter implements LoggingWriter {

	@Override
	public void init() {
		// Do nothing
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		// Just ignore
	}

}
