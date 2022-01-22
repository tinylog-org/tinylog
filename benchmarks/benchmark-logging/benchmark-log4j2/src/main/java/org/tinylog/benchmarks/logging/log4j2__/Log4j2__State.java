package org.tinylog.benchmarks.logging.log4j2__;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.core.AbstractLoggingState;
import org.tinylog.benchmarks.logging.core.LocationInfo;

/**
 * State for initializing Logback.
 */
@State(Scope.Thread)
public class Log4j2__State extends AbstractLoggingState {

	/**
	 * Configured logger for {@link Log4j2__Benchmark}.
	 */
	Logger logger;

	@Param
	private LocationInfo locationInfo;

	/** */
	public Log4j2__State() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws IOException {
		String file = createLogFile("log4j");
		byte[] configuration = createConfiguration(file).getBytes(Charset.defaultCharset());
		ByteArrayInputStream stream = new ByteArrayInputStream(configuration);
		ConfigurationSource source = new ConfigurationSource(stream);
		Configurator.initialize(null, source);

		logger = LogManager.getLogger(Log4j2__Benchmark.class);
	}

	/**
	 * Creates the XML configuration for Apache Log4j 2.
	 *
	 * @param file The path to the log file
	 * @return The configuration for Apache Log4j 2 as XML
	 */
	private String createConfiguration(String file) {
		StringBuilder xml = new StringBuilder();

		xml.append("<Configuration>");

		xml.append("<Appenders>");
		xml.append("<RandomAccessFile");
		xml.append(" name=\"file\"");
		xml.append(" fileName=\"").append(file).append("\"");
		xml.append(" immediateFlush=\"false\"");
		xml.append(">");
		xml.append("<PatternLayout><Pattern>");
		if (locationInfo == LocationInfo.FULL) {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %class.%method\\(\\) - %level: %message%n");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %logger - %level: %message%n");
		} else {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %level: %message%n");
		}
		xml.append("</Pattern></PatternLayout>");
		xml.append("</RandomAccessFile>");
		xml.append("</Appenders>");

		xml.append("<Loggers>");
		xml.append("<AsyncRoot");
		xml.append(" level=\"info\"");
		xml.append(" includeLocation=\"").append(locationInfo == LocationInfo.FULL).append("\"");
		xml.append(">");
		xml.append("<AppenderRef ref=\"file\" />");
		xml.append("</AsyncRoot>");
		xml.append("</Loggers>");

		xml.append("</Configuration>");

		return xml.toString();
	}

}
