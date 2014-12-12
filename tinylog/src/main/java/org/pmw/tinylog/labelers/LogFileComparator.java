/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.labelers;

import java.io.File;
import java.util.Comparator;

/**
 * Compares files by the date of last modification.
 */
final class LogFileComparator implements Comparator<File> {

	private static final LogFileComparator INSTANCE = new LogFileComparator();

	/**
	 * Get the instance of file comparator.
	 * 
	 * @return Instance of file comparator
	 */
	public static LogFileComparator getInstance() {
		return INSTANCE;
	}

	@Override
	public int compare(final File file1, final File file2) {
		long diff = file2.lastModified() - file1.lastModified();
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return +1;
		} else {
			return 0;
		}
	}

}
