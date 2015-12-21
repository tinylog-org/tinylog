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

package org.tinylog.labelers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.tinylog.Configuration;
import org.tinylog.EnvironmentHelper;
import org.tinylog.InternalLogger;

/**
 * Add the process ID (PID) to log files.
 */
@PropertiesSupport(name = "pid")
public final class ProcessIdLabeler implements Labeler {

	private final String pid;
	private LogFileFilter logFileFilter;

	/**
	 */
	public ProcessIdLabeler() {
		pid = EnvironmentHelper.getProcessId();
	}

	/**
	 * Get the process ID (PID).
	 *
	 * @return Process ID (PID).
	 */
	public String getProcessId() {
		return pid;
	}

	@Override
	public void init(final Configuration configuration) {
		// Nothing to do
	}

	@Override
	public File getLogFile(final File baseFile) {
		String directory = baseFile.getAbsoluteFile().getParent();
		String name = baseFile.getName();
		int index = name.indexOf('.', 1);
		if (index > 0) {
			String filenameWithoutExtension = name.substring(0, index);
			String filenameExtension = name.substring(index);
			logFileFilter = new LogFileFilter(filenameWithoutExtension, filenameExtension);
			return new File(directory, filenameWithoutExtension + "." + pid + filenameExtension);
		} else {
			logFileFilter = new LogFileFilter(name, "");
			return new File(directory, name + "." + pid);
		}
	}

	@Override
	public FilePair roll(final File file, final int maxBackups) throws IOException {
		if (file.exists()) {
			if (!file.delete()) {
				InternalLogger.warn("Failed to delete \"{}\"", file);
			}
		}

		File[] files = file.getAbsoluteFile().getParentFile().listFiles(logFileFilter);
		if (files != null && files.length > maxBackups) {
			Arrays.sort(files, LogFileComparator.getInstance());
			for (int i = maxBackups; i < files.length; ++i) {
				File backup = files[i];
				if (!backup.delete()) {
					InternalLogger.warn("Failed to delete \"{}\"", backup);
				}
			}
		}

		return new FilePair(file, null);
	}

}
