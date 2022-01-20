package org.tinylog.benchmarks.logging.tinylog2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.configuration.Configuration;

@State(Scope.Thread)
public class Tinylog2State {

	private static final String FORMAT_PATTERN =
		"{date:yyyy-MM-dd HH:mm:ss} - {thread} - {class}.{method}() - {level}: {message}";

	@Setup(Level.Trial)
	public void init() throws IOException {
		Path path = Files.createTempFile("benchmark_tinylog2_", ".log");
		path.toFile().deleteOnExit();

		Configuration.set("level", "INFO");
		Configuration.set("writer", "file");
		Configuration.set("writer.file", path.toString());
		Configuration.set("writer.format", FORMAT_PATTERN);
	}

}
