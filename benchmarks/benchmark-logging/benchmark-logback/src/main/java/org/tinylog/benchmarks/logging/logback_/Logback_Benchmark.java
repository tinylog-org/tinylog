package org.tinylog.benchmarks.logging.logback_;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;
import org.tinylog.benchmarks.logging.core.LocationInfo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Benchmark for issuing log entries with Logback.
 */
@State(Scope.Thread)
public class Logback_Benchmark extends AbstractLoggingBenchmark {

	@Param
	private LocationInfo locationInfo;

	private Logger logger;

	/** */
	public Logback_Benchmark() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws JoranException, IOException {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.reset();

		String file = createLogFile("logback");
		byte[] configuration = createConfiguration(file).getBytes(Charset.defaultCharset());

		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		configurator.doConfigure(new ByteArrayInputStream(configuration));

		logger = LoggerFactory.getLogger(Logback_Benchmark.class);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard() {
		logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output() {
		logger.info("Hello {}!", MAGIC_NUMBER);
	}

	/**
	 * Creates the XML configuration for Logback.
	 *
	 * @param file The path to the log file
	 * @return The configuration for Logback as XML
	 */
	private String createConfiguration(String file) {
		StringBuilder xml = new StringBuilder();

		xml.append("<configuration>");

		xml.append("<appender name=\"FILE\" class=\"ch.qos.logback.core.FileAppender\">");
		xml.append("<file>").append(file).append("</file>");
		xml.append("<immediateFlush>false</immediateFlush>");
		xml.append("<encoder><pattern>");
		if (locationInfo == LocationInfo.FULL) {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %class.%method\\(\\) - %level: %message%n");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %logger - %level: %message%n");
		} else {
			xml.append("%date{yyyy-MM-dd HH:mm:ss} - %thread - %level: %message%n");
		}
		xml.append("</pattern></encoder>");
		xml.append("</appender>");

		xml.append("<appender name=\"ASYNC\" class=\"ch.qos.logback.classic.AsyncAppender\">");
		xml.append("<discardingThreshold>0</discardingThreshold>");
		xml.append("<includeCallerData>").append(locationInfo == LocationInfo.FULL).append("</includeCallerData>");
		xml.append("<appender-ref ref=\"FILE\" />");
		xml.append("</appender>");

		xml.append("<root level=\"INFO\">");
		xml.append("<appender-ref ref=\"ASYNC\" />");
		xml.append("</root>");

		xml.append("</configuration>");

		return xml.toString();
	}

}
