package org.tinylog.impl.policy;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EndlessPolicyTest {

    @Mock
    private Path path;

    /**
     * Verifies that a log file can be continued without accessing the log file itself.
     */
    @Test
    void canContinueFile() {
        EndlessPolicy policy = new EndlessPolicy();

        assertThat(policy.canContinueFile(path)).isTrue();
        verifyNoInteractions(path);
    }

    /**
     * Verifies that log entries of any size are accepted without accessing the log file.
     */
    @Test
    void canAcceptLogEntry() {
        EndlessPolicy policy = new EndlessPolicy();
        policy.init(path);

        assertThat(policy.canAcceptDataRecord(Integer.MAX_VALUE)).isTrue();
        verifyNoInteractions(path);
    }

}
