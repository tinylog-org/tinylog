package org.tinylog.impl.format.placeholder;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for printing the exception including its stack trace for a log entry.
 */
public class ExceptionPlaceholder implements Placeholder {

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	/** */
	public ExceptionPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.EXCEPTION);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Throwable throwable = entry.getException();

		if (throwable != null) {
			appendThrowable(builder, "", throwable, EMPTY_STACK_TRACE);
		}
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		Throwable throwable = entry.getException();

		if (throwable == null) {
			return new SqlRecord<>(Types.LONGVARCHAR, null);
		} else {
			StringBuilder builder = new StringBuilder();
			appendThrowable(builder, "", throwable, EMPTY_STACK_TRACE);
			return new SqlRecord<>(Types.LONGVARCHAR, builder);
		}
	}

	/**
	 * Appends a throwable to a string builder.
	 *
	 * @param builder The target string builder
	 * @param prefix The prefix to add to each line
	 * @param throwable The throwable to append
	 * @param parentStackTrace The stack trace of the parent throwable
	 */
	private void appendThrowable(
		StringBuilder builder,
		String prefix,
		Throwable throwable,
		StackTraceElement[] parentStackTrace
	) {
		String message = throwable.getMessage();
		StackTraceElement[] stackTrace = throwable.getStackTrace();

		builder.append(throwable.getClass().getName());

		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}

		appendStackTrace(builder, prefix, parentStackTrace, stackTrace);
		appendSuppression(builder, prefix, throwable, stackTrace);
		appendCause(builder, prefix, throwable, stackTrace);
	}

	/**
	 * Appends the stack trace to a string builder.
	 *
	 * @param builder The target string builder
	 * @param prefix The prefix to add to each line
	 * @param parentStackTrace The stack trace of the parent throwable
	 * @param stackTrace The stack trace to append
	 */
	private void appendStackTrace(
		StringBuilder builder,
		String prefix,
		StackTraceElement[] parentStackTrace,
		StackTraceElement[] stackTrace
	) {
		int commonElements = 0;

		for (int i = 0; i < stackTrace.length && i < parentStackTrace.length; ++i) {
			StackTraceElement element = stackTrace[stackTrace.length - i - 1];
			StackTraceElement parentElement = parentStackTrace[parentStackTrace.length - i - 1];

			if (element.equals(parentElement)) {
				commonElements += 1;
			} else {
				break;
			}
		}

		for (int i = 0; i < stackTrace.length - commonElements; ++i) {
			StackTraceElement element = stackTrace[i];

			builder.append(System.lineSeparator());
			builder.append(prefix);
			builder.append("\tat ");
			builder.append(element);
		}

		if (commonElements > 0) {
			builder.append(System.lineSeparator());
			builder.append(prefix);
			builder.append("\t... ");
			builder.append(commonElements);
			builder.append(" more");
		}
	}

	/**
	 * Appends all suppressed throwables of the passed throwable to a string builder.
	 *
	 * @param builder The target string builder
	 * @param prefix The prefix to add to each line
	 * @param throwable The source throwable of the suppressed throwables to append
	 * @param stackTrace The stack trace of the parent throwable
	 */
	private void appendSuppression(
		StringBuilder builder,
		String prefix,
		Throwable throwable,
		StackTraceElement[] stackTrace
	) {
		for (Throwable suppressed : throwable.getSuppressed()) {
			builder.append(System.lineSeparator());
			builder.append(prefix);
			builder.append("\tSuppressed: ");
			appendThrowable(builder, prefix + "\t", suppressed, stackTrace);
		}
	}

	/**
	 * Appends the cause of the passed throwable to a string builder.
	 *
	 * <p>
	 *     If the cause throwable is {@code null}, nothing will be appended to the passed string builder.
	 * </p>
	 *
	 * @param builder The target string builder
	 * @param prefix The prefix to add to each line
	 * @param throwable The parent throwable of the cause throwable to append
	 * @param stackTrace The stack trace of the parent throwable
	 */
	private void appendCause(
		StringBuilder builder,
		String prefix,
		Throwable throwable,
		StackTraceElement[] stackTrace
	) {
		Throwable cause = throwable.getCause();
		if (cause != null) {
			builder.append(System.lineSeparator());
			builder.append(prefix);
			builder.append("Caused by: ");
			appendThrowable(builder, prefix, cause, stackTrace);
		}
	}

}
