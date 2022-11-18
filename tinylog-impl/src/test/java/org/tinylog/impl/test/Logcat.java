package org.tinylog.impl.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper class to access logcat.
 */
public final class Logcat {

    private final Pattern whitelist;
    private final Pattern blacklist;

    /** */
    public Logcat() {
        long pid = android.os.Process.myPid();
        long tid = android.os.Process.myTid();

        whitelist = Pattern.compile(String.format("\\W+%d\\W+%d\\W+", pid, tid));
        blacklist = Pattern.compile("\\W+(AndroidJUnit5|System|TestExecutor|TestLoader|TestRunner)\\W+");
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
    public List<String> fetchOutput() throws IOException {
        Process process = Runtime.getRuntime().exec("logcat -d -v threadtime");
        try (InputStream stream = process.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                return reader.lines()
                    .filter(line -> whitelist.matcher(line).find() && !blacklist.matcher(line).find())
                    .collect(Collectors.toList());
            }
        } finally {
            process.destroy();
        }
    }

}
