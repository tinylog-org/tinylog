package org.tinylog.benchmarks.logging.core;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.openjdk.jmh.Main;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    /**
     * Verifies that JMH will be executed correctly.
     *
     * @param out The captured output of the standard output stream
     */
    @Test
    @StdIo
    void executeJmh(StdOut out) throws URISyntaxException, IOException {
        Main.main(new String[] {"-h"});
        String expectedOutput = out.capturedString();

        Application.main(new String[] {"-h"});
        String actualOutput = out.capturedString().substring(expectedOutput.length());

        assertThat(actualOutput)
            .contains("JMH")
            .isEqualTo(expectedOutput);
    }

}
