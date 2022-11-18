package org.tinylog.impl.path;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.path.segments.PathSegment;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class PathParserTest {

    @Inject
    private Framework framework;

    @Inject
    private Log log;

    /**
     * Verifies that an empty path can be parsed.
     */
    @Test
    void empty() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("");
        assertThat(render(segments)).isEqualTo("");
    }

    /**
     * Verifies that a static path without any placeholders can be parsed.
     */
    @Test
    void staticPath() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("foo/log.txt");
        assertThat(render(segments)).isEqualTo("foo/log.txt");
    }

    /**
     * Verifies that curly brackets can be escaped.
     */
    @Test
    void escaped() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("log.'{foo}'.txt");
        assertThat(render(segments)).isEqualTo("log.{foo}.txt");
    }

    /**
     * Verifies that a path with a single placeholder without any configuration value can be parsed.
     */
    @Test
    void singlePlaceholderWithoutValue() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("foo/{date}.txt");
        assertThat(render(segments)).isEqualTo("foo/1970-01-01_00-00-00.txt");
    }

    /**
     * Verifies that a path with a single placeholder with a custom configuration value can be parsed.
     */
    @Test
    void singlePlaceholderWithValue() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("foo/{date: YYYY.MM.DD}.txt");
        assertThat(render(segments)).isEqualTo("foo/1970.01.01.txt");
    }

    /**
     * Verifies that a path with multiple placeholders can be parsed.
     */
    @Test
    void multiplePlaceholders() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("{process-id}/{date: YYYY.MM.DD}.txt");
        assertThat(render(segments)).isEqualTo(framework.getRuntime().getProcessId() + "/1970.01.01.txt");
    }

    /**
     * Verifies that an error will be logged for a path with a non-existent placeholder.
     */
    @Test
    void unknownPlaceholder() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("log.{foo}.txt");
        assertThat(render(segments)).isEqualTo("log.undefined.txt");
        assertThat(log.consume()).singleElement().satisfies(logEntry -> {
            assertThat(logEntry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(logEntry.getMessage()).contains("foo");
        });
    }

    /**
     * Verifies that an error will be logged for a path with placeholder having an invalid configuration value.
     */
    @Test
    void invalidPlaceholder() throws Exception {
        List<PathSegment> segments = new PathParser(framework).parse("log.{date: foo}.txt");
        assertThat(render(segments)).isEqualTo("log.undefined.txt");
        assertThat(log.consume()).singleElement().satisfies(logEntry -> {
            assertThat(logEntry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(logEntry.getMessage()).contains("date: foo");
        });
    }

    /**
     * Resolves the passed path segments as string.
     *
     * @param segments The path segments to resolve
     * @return The resolved path segment
     */
    private String render(List<PathSegment> segments) throws Exception {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        StringBuilder builder = new StringBuilder();
        for (PathSegment segment : segments) {
            segment.resolve(builder, date);
        }
        return builder.toString();
    }

}
