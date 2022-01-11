package org.tinylog.benchmarks.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * State for writing strings by using {@link FileOutputStream}.
 */
@State(Scope.Thread)
public class OutputStreamState extends AbstractState<OutputStream> {

	/**
	 * The buffer sizes to benchmark.
	 */
	@Param({ "0", "1024", "2048", "4096", "8192", "16384", "32768", "65536", "131072" })
	private int bufferSize;

	/** */
	public OutputStreamState() {
	}

	@Override
	public void write(String content) throws IOException {
		instance.write(content.getBytes(CHARSET));
	}

	@Override
	protected OutputStream create(Path path) throws IOException {
		OutputStream stream = new FileOutputStream(path.toFile());

		if (bufferSize > 0) {
			return new BufferedOutputStream(stream, bufferSize);
		}

		return stream;
	}

}
