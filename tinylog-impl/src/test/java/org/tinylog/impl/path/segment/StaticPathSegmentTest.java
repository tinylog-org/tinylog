package org.tinylog.impl.path.segment;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StaticPathSegmentTest {

    @TempDir
    private Path folder;

    @Mock
    private Supplier<ZonedDateTime> dateTimeSupplier;

    /**
     * Verifies that the static path segment appends the stored text data to the given directory and prefix.
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
        new StaticPathSegment("foo").resolve(builder, dateTimeSupplier);
        assertThat(builder).asString().isEqualTo("bar/foo");
    }

}
