package org.tinylog.impl.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.writers.Writer;

/**
 * Repository for writers.
 */
public class WriterRepository {

    private final Set<LogEntryValue> requiredLogEntryValues;
    private final List<Writer> writers;

    /**
     * @param writers The writers to store
     */
    public WriterRepository(Collection<Writer> writers) {
        this.requiredLogEntryValues = EnumSet.noneOf(LogEntryValue.class);
        this.writers = new ArrayList<>(writers);

        for (Writer writer : writers) {
            requiredLogEntryValues.addAll(writer.getRequiredLogEntryValues());
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
    public Collection<Writer> getWriters() {
        return writers;
    }

}
