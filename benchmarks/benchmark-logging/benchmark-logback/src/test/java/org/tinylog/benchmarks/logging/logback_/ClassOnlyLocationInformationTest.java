package org.tinylog.benchmarks.logging.logback_;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.tinylog.benchmarks.logging.core.LocationInfo;
import org.tinylog.benchmarks.logging.logback_.util.LogbackUtil;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ClassOnlyLocationInformationTest {

	private static final String NEW_LINE = System.lineSeparator();

	private static Logback_Benchmark benchmark;

	/**
	 * Initializes the benchmark including the logging framework.
	 *
	 * @throws Exception Failed to configure the logging framework
	 */
	@BeforeAll
	static void init() throws Exception {
		benchmark = new Logback_Benchmark(LocationInfo.CLASS_OR_CATEGORY_ONLY);
		benchmark.configure();
	}

	/**
	 * Shuts the benchmark including the logging framework gracefully down.
	 */
	@AfterAll
	static void dispose() {
		benchmark.shutdown();
	}

	/**
	 * Verifies that the debug log entry will be not output.
	 */
	@Test
	@Order(1)
	void discard() throws InterruptedException, IOException {
		benchmark.discard();
		LogbackUtil.flush();

		String logFile = benchmark.getLogFile();
		assertThat(logFile).isNotNull();

		Path path = Paths.get(logFile);
		assertThat(path).isEmptyFile();
	}

	/**
	 * Verifies that the into log entry will be output correctly.
	 */
	@Test
	@Order(2)
	void output() throws InterruptedException, IOException {
		benchmark.output();
		LogbackUtil.flush();

		String logFile = benchmark.getLogFile();
		assertThat(logFile).isNotNull();

		Path path = Paths.get(logFile);
		assertThat(path)
			.content(StandardCharsets.UTF_8)
			.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} - main - org\\.tinylog\\.benchmarks\\.logging\\."
				+ "logback_\\.Logback_Benchmark - INFO: Hello 42!" + NEW_LINE);
	}

}
