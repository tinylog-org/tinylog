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
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.pmw.tinylog.Logger;

/**
 * Add a timestamp to the real log file and the backups.
 */
public final class TimestampLabeller implements Labeller {

	private static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH-mm-ss";

	private final String timestampFormat;
	private final LogFileFilter logFileFilter;
	private final LogFileComparator logFileComparator;

	private String filenameWithoutExtension;
	private String filenameExtension;

	/**
	 * Use the default timestamp: yyyy-MM-dd HH-mm-ss.
	 */
	public TimestampLabeller() {
		this(DEFAULT_TIMESTAMP_FORMAT);
	}

	/**
	 * Timestamp pattern is compatible with {@link SimpleDateFormat#SimpleDateFormat(String)}.
	 * 
	 * @param timestampFormat
	 *            Timestamp pattern for formatting the time-based identify
	 */
	public TimestampLabeller(final String timestampFormat) {
		this.timestampFormat = timestampFormat;
		this.logFileFilter = new LogFileFilter();
		this.logFileComparator = new LogFileComparator();
	}

	/**
	 * Returns the name of the labeller.
	 * 
	 * @return "timestamp"
	 */
	public static String getName() {
		return "timestamp";
	}

	@Override
	public File getLogFile(final File baseFile) {
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

		return createFile();
	}

	@Override
	public File roll(final File file, final int maxBackups) {
		List<File> files = Arrays.asList(file.getAbsoluteFile().getParentFile().listFiles(logFileFilter));
		if (files.size() > maxBackups) {
			Collections.sort(files, logFileComparator);
			for (int i = maxBackups; i < files.size(); ++i) {
				files.get(i).delete();
			}
		}

		return createFile();
	}

	private File createFile() {
		return new File(filenameWithoutExtension + "." + new SimpleDateFormat(timestampFormat, Logger.getLocale()).format(new Date()) + filenameExtension);
	}

	private final class LogFileFilter implements FileFilter {

		@Override
		public boolean accept(final File file) {
			String path = file.getAbsolutePath();
			return path.startsWith(filenameWithoutExtension) && path.endsWith(filenameExtension);
		}

	}

	private final class LogFileComparator implements Comparator<File> {

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

}
