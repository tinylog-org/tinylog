package org.tinylog.impl.path.segment;

import java.time.ZonedDateTime;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
@ExtendWith(MockitoExtension.class)
class CountSegmentBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    @Mock
    private Supplier<ZonedDateTime> dateTimeSupplier;

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

    /**
     * Verifies that the builder creates the expected path segment and does not output any internal log entries if the
     * configuration is empty as expected.
     */
    @Test
    void creationWithoutConfigurationValue() throws Exception {
        StringBuilder builder = new StringBuilder("bar/");
        new CountSegmentBuilder().create(context, null).resolve(builder, dateTimeSupplier);
        assertThat(builder).asString().isEqualTo("bar/0");
    }

    /**
     * Verifies that the builder creates the expected path segment and does output an internal warning log entry if the
     * configuration is unexpectedly not empty.
     */
    @Test
    void creationWithConfigurationValue() throws Exception {
        StringBuilder builder = new StringBuilder("bar/");
        new CountSegmentBuilder().create(context, "foo").resolve(builder, dateTimeSupplier);
        assertThat(builder).asString().isEqualTo("bar/0");

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

}
