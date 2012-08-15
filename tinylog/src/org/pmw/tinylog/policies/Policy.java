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

import java.io.File;

import org.pmw.tinylog.ELoggingLevel;

/**
 * Policies define rollover strategies for {@link org.pmw.tinylog.writers.RollingFileWriter RollingFileWriter} .
 */
public interface Policy {

	/**
	 * Determine if a rollover should occur at startup.
	 * 
	 * @param logFile
	 *            Log file to continue
	 * @return <code>true</code> to continue the log file, <code>false</code> to trigger a rollover
	 */
	boolean initCheck(File logFile);

	/**
	 * Determine if a rollover should occur.
	 * 
	 * @param level
	 *            Logging level of log entry to write
	 * @param logEntry
	 *            Log entry to write
	 * @return <code>true</code> to continue the current log file, <code>false</code> to trigger a rollover
	 */
	boolean check(ELoggingLevel level, String logEntry);

	/**
	 * The log file was rolled and reset the policy.
	 */
	void reset();

}
