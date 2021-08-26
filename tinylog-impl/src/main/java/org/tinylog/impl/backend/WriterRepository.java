package org.tinylog.impl.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

/**
 * Repository for sync and async writers.
 */
public class WriterRepository {

	private final List<Writer> syncWriters;
	private final List<AsyncWriter> asyncWriters;
	private final List<Writer> allWriters;

	/**
	 * @param writers The writers to store
	 */
	public WriterRepository(Collection<Writer> writers) {
		allWriters = new ArrayList<>();
		syncWriters = new ArrayList<>();
		asyncWriters = new ArrayList<>();

		for (Writer writer : writers) {
			allWriters.add(writer);

			if (writer instanceof AsyncWriter) {
				asyncWriters.add((AsyncWriter) writer);
			} else {
				syncWriters.add(writer);
			}
		}
	}

	/**
	 * Gets all writers.
	 *
	 * @return All stored writers
	 */
	public Collection<Writer> getAllWriters() {
		return allWriters;
	}

	/**
	 * Gets all sync writers.
	 *
	 * @return All stored writes that do not implement the {@link AsyncWriter} interface
	 */
	public Collection<Writer> getSyncWriters() {
		return syncWriters;
	}

	/**
	 * Gets all async writers.
	 *
	 * @return All stored writes that implement the {@link AsyncWriter} interface
	 */
	public Collection<AsyncWriter> getAsyncWriters() {
		return asyncWriters;
	}

}
