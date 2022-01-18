package org.tinylog.benchmarks.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * State for writing strings by using {@link RandomAccessFile}.
 */
@State(Scope.Thread)
public class RandomAccessFileState extends AbstractState<RandomAccessFile> {

	/** */
	public RandomAccessFileState() {
	}

	@Override
	public void write(String content) throws IOException {
		instance.writeUTF(content);
	}

	@Override
	protected RandomAccessFile create(Path path) throws IOException {
		return new RandomAccessFile(path.toFile(), "rw");
	}

}
