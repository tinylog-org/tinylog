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

package org.pmw.tinylog.writers;

import org.pmw.tinylog.Logger;
import org.pmw.tinylog.LoggingLevel;

/**
 * Logging writers output created log entries from {@link Logger}.
 * 
 * <p>
 * The annotation {@link PropertiesSupport} must be added to the implemented writer class and the implemented writer
 * must be registered as service in "META-INF/services/org.pmw.tinylog.writers" in order to make the writer available by
 * properties files and system properties.
 * </p>
 * 
 * <p>
 * Example:<br />
 * <code>
 * {@literal @}PropertiesSupport(name = "example",
 * properties = { {@literal @}Property(name = "filename", type = String.class), {@literal @}Property(name = "backups", type = int.class) })<br />
 * public final class ExampleWriter implements LoggingWriter {
 * </code>
 * </p>
 * 
 * <p>
 * A logging writer must have a constructor that matches to the defined properties.
 * </p>
 */
public interface LoggingWriter {

	/**
	 * Initialize the writer.
	 */
	void init();

	/**
	 * Write a log entry.
	 * 
	 * @param level
	 *            Logging level
	 * @param logEntry
	 *            Log entry to output
	 */
	void write(LoggingLevel level, String logEntry);

}
