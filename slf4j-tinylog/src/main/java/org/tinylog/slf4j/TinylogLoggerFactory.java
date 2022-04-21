package org.tinylog.slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;

/**
 * Logger factory implementation for providing {@link TinylogLogger} instances.
 */
public final class TinylogLoggerFactory implements ILoggerFactory {

	private final Framework framework;
	private final ConcurrentMap<String, TinylogLogger> loggers;

	/**
	 * @param framework The actual logging framework
	 */
	public TinylogLoggerFactory(Framework framework) {
		this.framework = framework;
		this.loggers = new ConcurrentHashMap<>();
	}

	@Override
	public TinylogLogger getLogger(final String name) {
		return loggers.computeIfAbsent(name, this::createNewLogger);
	}

	/**
	 * Creates a new instance of {@link TinylogLogger}.
	 *
	 * @param name The category name for the new logger
	 * @return A new instance of {@link TinylogLogger}
	 */
	private TinylogLogger createNewLogger(String name) {
		return new TinylogLogger(name, framework);
	}

}
