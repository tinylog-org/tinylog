package org.tinylog.benchmarks.logging.jul_____;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.tinylog.benchmarks.logging.core.LocationInfo;

/**
 * Simple formatter for formatting log records for java.util.logging.
 */
public class SimpleFormatter extends Formatter {

	private static final String NEW_LINE = System.lineSeparator();
	private static final String SEPARATOR = " - ";

	private final LocationInfo locationInfo;
	private final SimpleDateFormat formatter;

	/**
	 * @param locationInfo The location information to output
	 */
	public SimpleFormatter(final LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
		this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	}

	@Override
	public String format(final LogRecord record) {
		StringBuilder builder = new StringBuilder();

		synchronized (formatter) {
			builder.append(formatter.format(new Date(record.getMillis())));
		}

		builder.append(SEPARATOR);
		builder.append(Thread.currentThread().getName());

		if (locationInfo == LocationInfo.FULL) {
			builder.append(SEPARATOR);
			builder.append(record.getSourceClassName());
			builder.append(".");
			builder.append(record.getSourceMethodName());
			builder.append("()");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			builder.append(SEPARATOR);
			builder.append(record.getLoggerName());
		}

		builder.append(SEPARATOR);
		builder.append(record.getLevel());
		builder.append(": ");
		builder.append(formatMessage(record));

		if (record.getThrown() != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			printWriter.println();
			record.getThrown().printStackTrace(printWriter);
			printWriter.close();
			builder.append(stringWriter);
		}

		builder.append(NEW_LINE);

		return builder.toString();
	}

}
