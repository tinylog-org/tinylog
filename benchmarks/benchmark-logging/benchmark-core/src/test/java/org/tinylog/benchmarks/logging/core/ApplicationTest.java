package org.tinylog.benchmarks.logging.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.Main;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    private PrintStream originalOutputStream;
    private PrintStream originalErrorStream;

    /**
     * Store original streams of {@link System#out} and {@link System#err}.
     */
    @BeforeEach
    void init() {
        originalOutputStream = System.out;
        originalErrorStream = System.err;
    }

    /**
     * Restores the original streams for {@link System#out} and {@link System#err}.
     */
    @AfterEach
    void reset() {
        System.setOut(originalOutputStream);
        System.setErr(originalErrorStream);
    }

    /**
     * Verifies that JMH will be executed correctly.
     */
    @Test
    void executeJmh() throws URISyntaxException, IOException {
        ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(expectedOutput));
        System.setErr(new PrintStream(expectedOutput));
        Main.main(new String[] {"-h"});

        ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutput));
        System.setErr(new PrintStream(actualOutput));
        Application.main(new String[] {"-h"});

        assertThat(actualOutput.toString())
            .contains("JMH")
            .isEqualTo(expectedOutput.toString());
    }

}
