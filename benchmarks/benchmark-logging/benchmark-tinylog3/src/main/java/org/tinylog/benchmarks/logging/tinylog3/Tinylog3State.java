package org.tinylog.benchmarks.logging.tinylog3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.core.Configuration;
import org.tinylog.core.Tinylog;

@State(Scope.Thread)
public class Tinylog3State {

	private static final String FORMAT_PATTERN =
		"{date:yyyy-MM-dd HH:mm:ss} - {thread} - {class}.{method}() - {level}: {message}";

	@Setup(Level.Trial)
	public void init() throws IOException {
		Path path = Files.createTempFile("benchmark_tinylog3_", ".log");
		path.toFile().deleteOnExit();

		Configuration configuration = Tinylog.getConfiguration();
		configuration.set("level", "INFO");
		configuration.set("writer.type", "file");
		configuration.set("writer.file", path.toString());
		configuration.set("writer.pattern", FORMAT_PATTERN);
	}

}
