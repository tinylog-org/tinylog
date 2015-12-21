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

package org.tinylog.labelers;

import java.io.File;
import java.io.IOException;

import org.tinylog.Configuration;

/**
 * A labeler names log files and backups for {@link org.tinylog.writers.RollingFileWriter RollingFileWriter}.
 *
 * <p>
 * The annotation {@link org.tinylog.labelers.PropertiesSupport PropertiesSupport} must be added to the implemented
 * labeler class and the implemented labeler must be registered as service in "META-INF/services/org.tinylog.labelers"
 * in order to make the labeler available by properties files and system properties.
 * </p>
 *
 * <p>
 * Example:<br>
 * <code>
 * {@literal @}PropertiesSupport(name = "count")<br>
 * public final class CountLabeler implements Labeler {
 * </code>
 * </p>
 *
 * <p>
 * A labeler must have a default constructor without any parameters. Optionally it can have an additional constructor
 * with a string parameter if the labeler supports parameters.
 * </p>
 */
public interface Labeler {

	/**
	 * Initialize the labeler.
	 *
	 * @param configuration
	 *            Configuration of logger
	 */
	void init(Configuration configuration);

	/**
	 * Returns the real log file.
	 *
	 * @param baseFile
	 *            Defined log file by user
	 * @return Real log file
	 */
	File getLogFile(File baseFile);

	/**
	 * Rolls existing log files and backups and returns a new log file and a backup of the previous one.
	 *
	 * @param file
	 *            Current log file
	 * @param maxBackups
	 *            Maximum number of backups to store
	 * @return New log file and backup of the previous one
	 *
	 * @throws IOException
	 *             Failed to roll log file
	 */
	FilePair roll(File file, int maxBackups) throws IOException;

}
