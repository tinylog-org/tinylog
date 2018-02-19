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

package org.pmw.tinylog.labelers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.InternalLogger;

/**
 * Adds a timestamp to the real log file and the backups.
 */
@PropertiesSupport(name = "timestamp")
public final class TimestampLabeler implements Labeler {

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH-mm-ss";

	private final String pattern;
	private TimestampFormatter formatter;

	private LogFileFilter logFileFilter;

	private String directory;
	private String filenameWithoutExtension;
	private String filenameExtension;

	/**
	 * Use the default timestamp: yyyy-MM-dd HH-mm-ss.
	 */
	public TimestampLabeler() {
		this(DEFAULT_TIMESTAMP_PATTERN);
	}

	/**
	 * Timestamp pattern is compatible with {@link SimpleDateFormat#SimpleDateFormat(String)}.
	 *
	 * @param pattern
	 *            Timestamp pattern for formatting the time-based identify
	 */
	public TimestampLabeler(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public void init(final Configuration configuration) {
		if (pattern.contains("SSSS") || pattern.contains("n") || pattern.contains("N")) {
			if (EnvironmentHelper.isAtLeastJava9()) {
				formatter = new PreciseTimestampFormatter(pattern, configuration.getLocale());
			} else {
				InternalLogger.warn("Java supports microseconds and nanoseconds only from version 9 onwards");
				formatter = new LegacyTimestampFormatter(pattern, configuration.getLocale());
			}
		} else {
			formatter = new LegacyTimestampFormatter(pattern, configuration.getLocale());
		}
	}

	@Override
	public File getLogFile(final File baseFile, final int maxBackups) {
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

		delete(baseFile.getAbsoluteFile().getParentFile().listFiles(logFileFilter), maxBackups);

		return createFile();
	}

	@Override
	public File roll(final File file, final int maxBackups) {
		delete(file.getAbsoluteFile().getParentFile().listFiles(logFileFilter), maxBackups);
		return createFile();
	}

	private File createFile() {
		return new File(directory, filenameWithoutExtension + "." + formatter.getCurrentTimestamp() + filenameExtension);
	}

	private void delete(final File[] files, final int maxBackups) {
		if (files != null && files.length > maxBackups) {
			Arrays.sort(files, LogFileComparator.getInstance());
			for (int i = maxBackups; i < files.length; ++i) {
				File backup = files[i];
				if (!backup.delete()) {
					InternalLogger.warn("Failed to delete \"{}\"", backup);
				}
			}
		}
	}

	private interface TimestampFormatter {

		String getCurrentTimestamp();

	}

	private static final class LegacyTimestampFormatter implements TimestampFormatter {

		private final DateFormat format;

		private LegacyTimestampFormatter(final String pattern, final Locale locale) {
			format = new SimpleDateFormat(pattern, locale);
		}

		public String getCurrentTimestamp() {
			return format.format(new Date());
		}

	}

	private static final class PreciseTimestampFormatter implements TimestampFormatter {

		private final DateTimeFormatter formatter;

		private PreciseTimestampFormatter(final String pattern, final Locale locale) {
			formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(ZoneId.systemDefault());
		}

		public String getCurrentTimestamp() {
			return formatter.format(Instant.now());
		}

	}

}
