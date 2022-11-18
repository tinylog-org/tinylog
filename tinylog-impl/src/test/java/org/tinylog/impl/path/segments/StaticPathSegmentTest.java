package org.tinylog.impl.path.segments;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class StaticPathSegmentTest {

    @TempDir
    private Path folder;

    /**
     * Verifies that the static path segment appends the stored text data to the passed directory and prefix.
     */
    @Test
    void findLatest() {
        String latest = new StaticPathSegment("bar").findLatest(folder, "foo");
        assertThat(latest).isEqualTo("bar");
    }

    /**
     * Verifies that the static path segment appends the stored text data to the passed string builder.
     */
    @Test
    void resolve() {
        StringBuilder builder = new StringBuilder("bar/");
        new StaticPathSegment("foo").resolve(builder, null);
        assertThat(builder).asString().isEqualTo("bar/foo");
    }

}
