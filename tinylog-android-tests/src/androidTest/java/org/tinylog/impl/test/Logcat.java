package org.tinylog.impl.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class to access logcat.
 */
public final class Logcat {

    /** */
    private Logcat() {
    }

    /**
     * Clears all existing Logcat output.
     *
     * @throws IOException Failed to call command line tool
     * @throws InterruptedException Failed to wait for command line tool
     */
    public static void clear() throws IOException, InterruptedException {
        Runtime.getRuntime().exec("logcat -c").waitFor();
    }

    /**
     * Fetches the output from logcat.
     *
     * <p>
     *     All generic log entries from test frameworks are removed automatically.
     * </p>
     *
     * @return Each list element represents one log entry line from logcat
     * @throws IOException Failed to read output of logcat
     */
    public static List<String> fetchOutput() throws IOException {
        Process process = Runtime.getRuntime().exec("logcat -d -v threadtime");
        try (InputStream stream = process.getInputStream()) {
            try (Reader inputReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputReader)) {
                    return bufferedReader.lines().collect(Collectors.toList());
                }
            }
        } finally {
            process.destroy();
        }
    }

}
