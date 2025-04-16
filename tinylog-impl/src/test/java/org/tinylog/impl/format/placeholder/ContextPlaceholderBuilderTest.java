package org.tinylog.impl.format.placeholder;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class ContextPlaceholderBuilderTest {

    @Inject
    private TinylogContext context;

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

    /**
     * Verifies that a multi context placeholder can be created if there is no thread context key.
     */
    @Test
    void creationWithoutConfigurationValue() {
        Placeholder placeholder = new ContextPlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(MultiContextPlaceholder.class);

        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").context("bar", "b").create();
        assertThat(placeholder.getValue(logEntry)).isEqualTo("bar=b, foo=a");
    }

    /**
     * Verifies that a single context placeholder can be created for a given thread context key.
     */
    @Test
    void creationWithConfigurationValue() {
        Placeholder placeholder = new ContextPlaceholderBuilder().create(context, "foo");
        assertThat(placeholder).isInstanceOf(SingleContextPlaceholder.class);

        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").context("bar", "b").create();
        assertThat(placeholder.getValue(logEntry)).isEqualTo("a");
    }

}
