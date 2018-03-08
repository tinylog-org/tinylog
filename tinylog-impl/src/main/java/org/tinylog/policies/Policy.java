/*
 * Copyright 2018 Martin Winandy
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

/**
 * Policies are used by {@link org.tinylog.writers.RollingFileWriter RollingFileWriter} for triggering rollover events.
 */
public interface Policy {

	/**
	 * Checks if an existing log file can be continued.
	 * 
	 * @param path
	 *            Path to log file
	 * @return {@code true} if existing log file can be continued, {@code false} if a new log file should be created
	 */
	boolean continueExistingFile(String path);

	/**
	 * Checks if a log entry can be appended to the current log file.
	 * 
	 * @param entry
	 *            Log entry
	 * @return {@code true} if log entry can be appended to the current log file, {@code false} if a new log file should
	 *         be created
	 */
	boolean continueCurrentFile(byte[] entry);

	/**
	 * Resets this policy as a new log file has been started.
	 */
	void reset();

}
