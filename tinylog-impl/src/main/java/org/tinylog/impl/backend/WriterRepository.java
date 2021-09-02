package org.tinylog.impl.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

/**
 * Repository for sync and async writers.
 */
public class WriterRepository {

	private final Set<LogEntryValue> requiredLogEntryValues;
	private final List<Writer> syncWriters;
	private final List<AsyncWriter> asyncWriters;
	private final List<Writer> allWriters;

	/**
	 * @param writers The writers to store
	 */
	public WriterRepository(Collection<Writer> writers) {
		requiredLogEntryValues = EnumSet.noneOf(LogEntryValue.class);
		allWriters = new ArrayList<>();
		syncWriters = new ArrayList<>();
		asyncWriters = new ArrayList<>();

		for (Writer writer : writers) {
			requiredLogEntryValues.addAll(writer.getRequiredLogEntryValues());
			allWriters.add(writer);

			if (writer instanceof AsyncWriter) {
				asyncWriters.add((AsyncWriter) writer);
			} else {
				syncWriters.add(writer);
			}
		}
	}

	/**
	 * Gets the required log entry values for all writers in this repository.
	 *
	 * @return All required log entry values
	 * @see Writer#getRequiredLogEntryValues()
	 */
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return requiredLogEntryValues;
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
