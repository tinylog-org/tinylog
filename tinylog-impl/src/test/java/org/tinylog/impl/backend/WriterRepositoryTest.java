package org.tinylog.impl.backend;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WriterRepositoryTest {

    /**
     * Verifies that all requires log entry values of all stored writers are combined correctly.
     */
    @Test
    void requiredLogEntryValues() {
        Writer first = mock(Writer.class);
        when(first.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.TIMESTAMP, LogEntryValue.MESSAGE));

        AsyncWriter second = mock(AsyncWriter.class);
        when(second.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.UPTIME, LogEntryValue.MESSAGE));

        Writer third = mock(Writer.class);
        when(third.getRequiredLogEntryValues()).thenReturn(EnumSet.of(LogEntryValue.CLASS, LogEntryValue.MESSAGE));

        WriterRepository repository = new WriterRepository(ImmutableList.of(first, second, third));
        assertThat(repository.getRequiredLogEntryValues()).containsExactlyInAnyOrder(
            LogEntryValue.TIMESTAMP, LogEntryValue.UPTIME, LogEntryValue.CLASS, LogEntryValue.MESSAGE
        );
    }

    /**
     * Verifies that sync and async writers are partitioning correctly.
     */
    @Test
    void partitioningOfWriters() {
        Writer first = mock(Writer.class);
        AsyncWriter second = mock(AsyncWriter.class);
        Writer third = mock(Writer.class);

        WriterRepository repository = new WriterRepository(ImmutableList.of(first, second, third));
        assertThat(repository.getAllWriters()).containsExactlyInAnyOrder(first, second, third);
        assertThat(repository.getSyncWriters()).containsExactlyInAnyOrder(first, third);
        assertThat(repository.getAsyncWriters()).containsExactlyInAnyOrder(second);
    }

}
