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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.InternalLogger;

/**
 * Adds a timestamp to the real log file and the backups.
 */
@PropertiesSupport(name = "timestamp")
public final class TimestampLabeller implements Labeller {

	private static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH-mm-ss";

	private final String timestampFormat;
	private DateFormat dateFormat;

	private LogFileFilter logFileFilter;

	private String directory;
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
	}

	@Override
	public void init(final Configuration configuration) {
		dateFormat = new SimpleDateFormat(timestampFormat, configuration.getLocale());
	}

	@Override
	public File getLogFile(final File baseFile) {
		directory = baseFile.getAbsoluteFile().getParent();
		String name = baseFile.getName();
		int index = name.indexOf('.', 1);
		if (index > 0) {
			filenameWithoutExtension = name.substring(0, index);
			filenameExtension = name.substring(index);
		} else {
			filenameWithoutExtension = name;
			filenameExtension = "";
		}

		logFileFilter = new LogFileFilter(filenameWithoutExtension, filenameExtension);

		return createFile();
	}

	@Override
	public File roll(final File file, final int maxBackups) {
		List<File> files = Arrays.asList(file.getAbsoluteFile().getParentFile().listFiles(logFileFilter));
		if (files.size() > maxBackups) {
			Collections.sort(files, LogFileComparator.getInstance());
			for (int i = maxBackups; i < files.size(); ++i) {
				File backup = files.get(i);
				if (!backup.delete()) {
					InternalLogger.warn("Failed to delete \"{0}\"", backup);
				}
			}
		}

		return createFile();
	}

	private File createFile() {
		return new File(directory, filenameWithoutExtension + "." + dateFormat.format(new Date()) + filenameExtension);
	}

}
