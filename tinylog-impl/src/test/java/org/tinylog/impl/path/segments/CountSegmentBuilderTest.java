package org.tinylog.impl.path.segments;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class CountSegmentBuilderTest {

    @Inject
    private LoggingContext context;

    @Inject
    private Log log;

    /**
     * Verifies that the builder creates the expected path segment and does not output any internal log entries if the
     * configuration is empty as expected.
     */
    @Test
    void creationWithoutConfigurationValue() throws Exception {
        StringBuilder builder = new StringBuilder("bar/");
        new CountSegmentBuilder().create(context, null).resolve(builder, null);
        assertThat(builder).asString().isEqualTo("bar/0");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that the builder creates the expected path segment and does output an internal warning log entry if the
     * configuration is unexpectedly not empty.
     */
    @Test
    void creationWithConfigurationValue() throws Exception {
        StringBuilder builder = new StringBuilder("bar/");
        new CountSegmentBuilder().create(context, "foo").resolve(builder, null);
        assertThat(builder).asString().isEqualTo("bar/0");

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getMessage()).contains("foo");
        });
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PathSegmentBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(CountSegmentBuilder.class);
            assertThat(builder.getName()).isEqualTo("count");
        });
    }

}
