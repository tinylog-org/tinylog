package org.tinylog.benchmarks.logging.logback_.util;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.FileAppender;

/**
 * Helper functions for testing Logback.
 */
public final class LogbackUtil {

	private static final int MILLISECONDS_TO_WAIT = 10;

	/** */
	private LogbackUtil() {
	}

	/**
	 * Flushes the file appender.
	 *
	 * @throws InterruptedException Failed to wait for the {@link AsyncAppender}
	 * @throws IOException Failed to flush the {@link OutputStream} of the file {@link FileAppender}
	 */
	public static void flush() throws InterruptedException, IOException {
		Thread.sleep(MILLISECONDS_TO_WAIT);

		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		AsyncAppender asyncAppender = (AsyncAppender) logger.getAppender("ASYNC");
		FileAppender<?> fileAppender = (FileAppender<?>) asyncAppender.getAppender("FILE");
		fileAppender.getOutputStream().flush();
	}

}
