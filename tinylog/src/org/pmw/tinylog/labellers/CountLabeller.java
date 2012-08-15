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
 * Numbers the backups sequentially: "0" for the newest, "1" for the second newest etc.
 */
public class CountLabeller implements Labeller {

	private String filenameWithoutExtension;
	private String filenameExtension;

	@Override
	public final File getLogFile(final File baseFile) {
		String path = baseFile.getPath();
		String name = baseFile.getName();
		int index = name.indexOf('.', 1);
		if (index > 0) {
			filenameWithoutExtension = path.substring(0, (path.length() - name.length()) + index);
			filenameExtension = name.substring(index);
		} else {
			filenameWithoutExtension = path;
			filenameExtension = "";
		}
		return baseFile;
	}

	/**
	 * Returns the name of the labeller.
	 * 
	 * @return "count"
	 */
	public static String getName() {
		return "count";
	}

	@Override
	public final File roll(final File file, final int maxBackups) {
		roll(file, 0, maxBackups);
		return file;
	}

	private void roll(final File sourceFile, final int number, final int maxBackups) {
		File targetFile = new File(filenameWithoutExtension + "." + number + filenameExtension);
		if (targetFile.exists()) {
			roll(targetFile, number + 1, maxBackups);
		}
		if (number < maxBackups) {
			sourceFile.renameTo(targetFile);
		} else {
			sourceFile.delete();
		}
	}

}
