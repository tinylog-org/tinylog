package org.tinylog.benchmarks.file;

import java.io.IOException;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.tinylog.impl.writers.file.LogFile;

/**
 * State for writing strings by using {@link LogFile}.
 */
@State(Scope.Thread)
public class LogFileState extends AbstractState<LogFile> {

	/**
	 * The buffer sizes to benchmark.
	 */
	@Param({ "1024", "2048", "4096", "8192", "16384", "32768", "65536", "131072" })
	private int bufferSize;

	/** */
	public LogFileState() {
	}

	/**
	 * @param bufferSize The buffer size in bytes
	 */
	public LogFileState(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public void write(String content) throws IOException {
		instance.write(content);
	}

	@Override
	protected LogFile create(Path path) throws IOException {
		return new LogFile(path.toString(), bufferSize, CHARSET, false);
	}

}
