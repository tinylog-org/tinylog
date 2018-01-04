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

package org.tinylog.path;

import java.util.Collection;

/**
 * Dynamic segment of a path to log files.
 */
public interface Segment {

	/**
	 * Gets the latest available log file.
	 * 
	 * @param prefix
	 *            Static path prefix that is already resolved
	 * @return Full path to the latest log file or {@code null} if there is none
	 */
	String getLatestFile(String prefix);

	/**
	 * Collects all existing log files.
	 * 
	 * @param prefix
	 *            Static path prefix that is already resolved
	 * @return All existing log files in any order
	 */
	Collection<String> getAllFiles(String prefix);

	/**
	 * Generates the full path for a new log file.
	 * 
	 * @param prefix
	 *            Static path prefix that is already resolved
	 * @return Full path for new log file
	 */
	String createNewFile(String prefix);

}
