package org.tinylog.core;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Tinylog
class ConfigurationBuilderTest {

    @Mock
    private Framework framework;

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that an existing value can be received.
     */
    @Test
    void getExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, Map.of("foo", "1"), logger);

        assertThat(builder.get("foo")).isEqualTo("1");
    }

    /**
     * Verifies that {@code null} is returned for a non-existent value.
     */
    @Test
    void getNonExistentValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap(), logger);

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that a new value can be set.
     */
    @Test
    void setNewValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap(), logger)
            .set("foo", "1");

        assertThat(builder.get("foo")).isEqualTo("1");
    }

    /**
     * Verifies that an already existing value can be overwritten.
     */
    @Test
    void overwriteExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, Map.of("foo", "1"), logger)
            .set("foo", "2");

        assertThat(builder.get("foo")).isEqualTo("2");
    }

    /**
     * Verifies that an existing value can be removed.
     */
    @Test
    void removeExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, Map.of("foo", "1"), logger)
            .remove("foo");

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that a non-existent value can be removed.
     */
    @Test
    void removeNonExistentValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap(), logger)
            .remove("foo");

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that the built configuration can be applied.
     */
    @Test
    void activate() {
        new ConfigurationBuilder(framework, Map.of("foo", "1"), logger).activate();

        ArgumentCaptor<Configuration> captor = ArgumentCaptor.forClass(Configuration.class);
        verify(framework).setConfiguration(captor.capture());

        Configuration configuration = captor.getValue();
        assertThat(configuration.getAllValues()).containsExactly(entry("foo", "1"));
    }

}
