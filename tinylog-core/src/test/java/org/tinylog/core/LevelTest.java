package org.tinylog.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LevelTest {

    /**
     * Verifies that the least severe level can be correctly evaluated.
     *
     * @param first The first severity level to pass
     * @param second The second severity level to pass
     * @param expected The expected severity level to return
     */
    @ParameterizedTest
    @CsvSource({
        "DEBUG, INFO , DEBUG",
        "INFO , INFO , INFO",
        "INFO , DEBUG, DEBUG"
    })
    void leastSevereLevel(Level first, Level second, Level expected) {
        assertThat(Level.leastSevereLevel(first, second)).isEqualTo(expected);
    }

    /**
     * Verifies that the most severe level can be correctly evaluated.
     *
     * @param first The first severity level to pass
     * @param second The second severity level to pass
     * @param expected The expected severity level to return
     */
    @ParameterizedTest
    @CsvSource({
        "DEBUG, INFO,  INFO",
        "DEBUG, DEBUG, DEBUG",
        "INFO , DEBUG, INFO"
    })
    void mostSevereLevel(Level first, Level second, Level expected) {
        assertThat(Level.mostSevereLevel(first, second)).isEqualTo(expected);
    }

}
