/*
 * Copyright 2014 Martin Winandy
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

/**
 * Pair of files (actual file and backup).
 */
public final class FilePair {

	private final File file;
	private final File backup;

	/**
	 * @param file
	 *            Actual file
	 * @param backup
	 *            Backup of previous file
	 */
	public FilePair(final File file, final File backup) {
		this.file = file;
		this.backup = backup;
	}

	/**
	 * Get the backup of the previous file.
	 *
	 * @return Backup of previous file
	 */
	public File getBackup() {
		return backup;
	}

	/**
	 * Get the actual file.
	 * 
	 * @return Actual file
	 */
	public File getFile() {
		return file;
	}

}
