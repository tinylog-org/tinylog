package org.tinylog.benchmarks.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Base state for file writing approaches.
 *
 * @param <T> The class to use for writing
 */
@SuppressWarnings("JmhInspections")
public abstract class AbstractState<T extends Closeable> implements Closeable {

	/**
	 * The charset to use for encoding strings as byte arrays.
	 */
	protected static final Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * The instance to use for writing.
	 */
	protected T instance;

	private Path path;

	/** */
	public AbstractState() {
	}

	/**
	 * Gets the path to the file.
	 *
	 * @return The path to the file
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * Creates a new temporary file and an instance of the class to use for writing.
	 *
	 * @throws IOException Failed to create or access the temporary file
	 */
	@Setup(Level.Iteration)
	public void init() throws IOException {
		path = Files.createTempFile("tinylog", null);
		path.toFile().deleteOnExit();

		instance = create(path);
	}

	/**
	 * Closes the instance to use for writing and deletes the temporary file.
	 *
	 * @throws IOException Failed to close the instance or to delete the temporary file
	 */
	@TearDown(Level.Iteration)
	public void dispose() throws IOException {
		close();
		Files.deleteIfExists(path);
	}

	/**
	 * Closes the temporary file.
	 *
	 * @throws IOException Failed to close the file properly.
	 */
	@Override
	public void close() throws IOException {
		instance.close();
	}

	/**
	 * Writes a string into the current temporary file.
	 *
	 * @param content The string to write
	 * @throws IOException Failed to write into the temporary file
	 */
	public abstract void write(String content) throws IOException;

	/**
	 * Creates a new instance of the class to use for writing.
	 *
	 * @param path The path of the file to use for writing
	 * @return The created instance
	 * @throws IOException Failed to access the temporary file
	 */
	protected abstract T create(Path path) throws IOException;

}
