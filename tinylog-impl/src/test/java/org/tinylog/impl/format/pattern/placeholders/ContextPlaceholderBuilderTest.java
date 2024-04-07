package org.tinylog.impl.format.pattern.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class ContextPlaceholderBuilderTest {

    @Inject
    private LoggingContext context;

    /**
     * Verifies that a context placeholder can be created without a thread context key.
     */
    @Test
    void creationWithoutConfigurationValue() {
        Placeholder placeholder = new ContextPlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(MultiValueContextPlaceholder.class);

        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").context("baz", "quk").create();
        assertThat(placeholder.getValue(logEntry)).isEqualTo("baz=quk, foo=bar");
    }

    /**
     * Verifies that a context placeholder can be created for a given thread context key.
     */
    @Test
    void creationWithConfigurationValue() {
        Placeholder placeholder = new ContextPlaceholderBuilder().create(context, "foo");
        assertThat(placeholder).isInstanceOf(SingleValueContextPlaceholder.class);

        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
        assertThat(placeholder.getValue(logEntry)).isEqualTo("bar");
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(ContextPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("context");
        });
    }

}
