package org.tinylog.impl.format.placeholder;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class ProcessIdPlaceholderBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private RuntimeFlavor runtime;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(ProcessIdPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("process-id");
        });
    }

    /**
     * Verifies that the builder can create an instance of {@link ProcessIdPlaceholder} without having a
     * configuration value.
     */
    @Test
    void creationWithoutConfigurationValue() {
        Placeholder placeholder = new ProcessIdPlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(ProcessIdPlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(runtime.getProcessId()));
    }

    /**
     * Verifies that the builder can create an instance of {@link ProcessIdPlaceholder} when having an unexpected
     * configuration value.
     */
    @Test
    void creationWithConfigurationValue() {
        Placeholder placeholder = new ProcessIdPlaceholderBuilder().create(context, "foo");
        assertThat(placeholder).isInstanceOf(ProcessIdPlaceholder.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(runtime.getProcessId()));
    }

}
