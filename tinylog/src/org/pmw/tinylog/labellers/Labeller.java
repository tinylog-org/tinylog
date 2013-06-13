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

package org.pmw.tinylog.labellers;

import java.io.File;

/**
 * A labeller names log files and backups for {@link org.pmw.tinylog.writers.RollingFileWriter RollingFileWriter}.
 * 
 * An implemented labeller must be registered as service in "META-INF/services/org.pmw.tinylog.labellers" and implement
 * the static method <code>public static String getName()</code>, so that tinylog can find it.
 */
public interface Labeller {

	/**
	 * Returns the real log file.
	 * 
	 * @param baseFile
	 *            Defined log file by user
	 * @return Real log file
	 */
	File getLogFile(File baseFile);

	/**
	 * Rolls existing log files and backups and returns a new log file.
	 * 
	 * @param file
	 *            Current log file
	 * @param maxBackups
	 *            Maximum number of backups to store
	 * @return New log file
	 */
	File roll(File file, int maxBackups);

}
