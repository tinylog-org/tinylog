package org.tinylog.impl.backend;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.impl.writers.Writer;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.tinylog.impl.LogEntryValue.CLASS;
import static org.tinylog.impl.LogEntryValue.MESSAGE;
import static org.tinylog.impl.LogEntryValue.TIMESTAMP;
import static org.tinylog.impl.LogEntryValue.UPTIME;

@ExtendWith(MockitoExtension.class)
class WriterRepositoryTest {

    @Mock
    private Writer firstWriter;

    @Mock
    private Writer secondWriter;

    @Mock
    private Writer thirdWriter;

    /**
     * Verifies that all requires log entry values of all stored writers are combined correctly.
     */
    @Test
    void requiredLogEntryValues() {
        when(firstWriter.getRequiredLogEntryValues()).thenReturn(EnumSet.of(TIMESTAMP, MESSAGE));
        when(secondWriter.getRequiredLogEntryValues()).thenReturn(EnumSet.of(UPTIME, MESSAGE));
        when(thirdWriter.getRequiredLogEntryValues()).thenReturn(EnumSet.of(CLASS, MESSAGE));

        WriterRepository repository = new WriterRepository(ImmutableList.of(firstWriter, secondWriter, thirdWriter));
        assertThat(repository.getRequiredLogEntryValues()).containsExactlyInAnyOrder(
            TIMESTAMP,
            UPTIME,
            CLASS,
            MESSAGE
        );
    }

    /**
     * Verifies that all passed writers will be provided.
     */
    @Test
    void fetchWriterInstances() {
        WriterRepository repository = new WriterRepository(ImmutableList.of(firstWriter, secondWriter, thirdWriter));
        assertThat(repository.getWriters()).containsExactlyInAnyOrder(
            firstWriter,
            secondWriter,
            thirdWriter
        );
    }

}
