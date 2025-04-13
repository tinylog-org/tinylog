package org.tinylog.impl.format.placeholder;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

}
