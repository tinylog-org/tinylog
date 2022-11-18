package org.tinylog.core.test.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Storage for captured outputs from {@link System#out} and {@link System#err}.
 */
public class Output {

    private List<Pattern> excludes;
    private List<String> lines;

    /** */
    public Output() {
        this.excludes = Collections.emptyList();
        this.lines = new ArrayList<>();
    }

    /**
     * Retrieves all stored output.
     *
     * @return All lines of the stored output
     */
    public Iterable<String> consume() {
        try {
            return lines;
        } finally {
            lines = new ArrayList<>();
        }
    }

    /**
     * Sets the regular expressions for lines to exclude from storing.
     *
     * @param excludes All lines matching with at least one of these passed exclude regular expressions will be
     *                 discarded silently
     */
    public void setExcludes(Pattern... excludes) {
        this.excludes = Arrays.asList(excludes);
    }

    /**
     * Appends a new line at the end of the stored output. If the passed line matches with at least one of the defined
     * exclude regular expressions, it will be discarded silently.
     *
     * @param line New line to store
     */
    void add(String line) {
        if (excludes.stream().noneMatch(pattern -> pattern.matcher(line).matches())) {
            lines.add(line);
        }
    }

}
