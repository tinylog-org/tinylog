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

import org.pmw.tinylog.AbstractTest;

/**
 * Abstract test class for labelers.
 * 
 * @see Labeler
 */
public abstract class AbstractLabelerTest extends AbstractTest {

	/**
	 * Generate a backup file for a given log file.
	 * 
	 * @param baseFile
	 *            Log file
	 * @param fileExtension
	 *            File extension of log file
	 * @param label
	 *            Label to include before file extension
	 * @return Backup file
	 */
	protected static File getBackupFile(final File baseFile, final String fileExtension, final String label) {
		String path = baseFile.getPath();
		if (fileExtension == null) {
			File file = new File(path + "." + label);
			file.deleteOnExit();
			return file;
		} else {
			File file = new File(path.substring(0, path.length() - fileExtension.length() - 1) + "." + label + "." + fileExtension);
			file.deleteOnExit();
			return file;
		}
	}

}
