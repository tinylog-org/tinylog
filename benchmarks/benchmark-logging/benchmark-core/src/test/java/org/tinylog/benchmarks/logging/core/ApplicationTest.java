package org.tinylog.benchmarks.logging.core;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.Main;
import org.tinylog.core.test.system.CaptureSystemOutput;
import org.tinylog.core.test.system.Output;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureSystemOutput
class ApplicationTest {

    @Inject
    private Output output;

    /**
     * Verifies that JMH will be executed correctly.
     */
    @Test
    void executeJmh() throws URISyntaxException, IOException {
        Main.main(new String[] {"-h"});
        Iterable<String> expectedOutput = output.consume();

        Application.main(new String[] {"-h"});
        Iterable<String> actualOutput = output.consume();

        assertThat(actualOutput)
            .anySatisfy(line -> assertThat(line).contains("JMH"))
            .isEqualTo(expectedOutput);
    }

}
