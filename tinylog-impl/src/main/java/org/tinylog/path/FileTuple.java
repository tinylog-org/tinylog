/*
 * Copyright 2020 Martin Winandy
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

import java.io.File;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Immutable tuple of original file and its backup.
 */
public final class FileTuple {

	private final File original;
	private final File backup;

	/**
	 * @param original
	 *            Original file
	 * @param backup
	 *            Backup file
	 */
	public FileTuple(final File original, final File backup) {
		this.original = original;
		this.backup = backup;
	}

	/**
	 * Gets the original file.
	 *
	 * @return Original file
	 */
	public File getOriginal() {
		return original;
	}

	/**
	 * Gets the backup file.
	 *
	 * @return Backup file
	 */
	public File getBackup() {
		return backup;
	}

	/**
	 * Gets the last modification time.
	 *
	 * @return Last modification time
	 */
	public long getLastModified() {
		return Math.max(original.lastModified(), backup.lastModified());
	}

	/**
	 * Deletes the original and backup file.
	 */
	public void delete() {
		if (original.isFile() && !original.delete()) {
			InternalLogger.log(Level.WARN, "Failed to delete log file '" + original + "'");
		}
		if (!backup.equals(original) && backup.isFile() && !backup.delete()) {
			InternalLogger.log(Level.WARN, "Failed to delete backup file '" + backup + "'");
		}
	}

}
