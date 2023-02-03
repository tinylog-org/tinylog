package org.tinylog.impl.format.pattern.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@CaptureLogEntries
class ContextPlaceholderBuilderTest {

    @Inject
    private LoggingContext context;

    /**
     * Verifies that an {@link IllegalArgumentException} with a meaningful message description will be thrown, if the
     * thread context key is missing.
     */
    @Test
    void creationWithoutConfigurationValue() {
        assertThatThrownBy(() -> new ContextPlaceholderBuilder().create(context, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("key");
    }

    /**
     * Verifies that a context placeholder can be created for a given thread context key.
     */
    @Test
    void creationWithConfigurationValue() {
        Placeholder placeholder = new ContextPlaceholderBuilder().create(context, "foo");
        assertThat(placeholder).isInstanceOf(ContextPlaceholder.class);

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
