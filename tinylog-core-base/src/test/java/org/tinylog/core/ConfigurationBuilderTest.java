package org.tinylog.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfigurationBuilderTest {

    @Mock
    private Framework framework;

    /**
     * Verifies that an existing value can be received.
     */
    @Test
    void getExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, singletonMap("foo", "1"));

        assertThat(builder.get("foo")).isEqualTo("1");
    }

    /**
     * Verifies that {@code null} is returned for a non-existent value.
     */
    @Test
    void getNonExistentValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap());

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that a new value can be set.
     */
    @Test
    void setNewValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap())
            .set("foo", "1");

        assertThat(builder.get("foo")).isEqualTo("1");
    }

    /**
     * Verifies that an already existing value can be overwritten.
     */
    @Test
    void overwriteExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, singletonMap("foo", "1"))
            .set("foo", "2");

        assertThat(builder.get("foo")).isEqualTo("2");
    }

    /**
     * Verifies that an existing value can be removed.
     */
    @Test
    void removeExistingValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, singletonMap("foo", "1"))
            .remove("foo");

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that a non-existent value can be removed.
     */
    @Test
    void removeNonExistentValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder(framework, emptyMap())
            .remove("foo");

        assertThat(builder.get("foo")).isNull();
    }

    /**
     * Verifies that the built configuration can be applied.
     */
    @Test
    void activate() {
        new ConfigurationBuilder(framework, singletonMap("foo", "1")).activate();

        ArgumentCaptor<Configuration> captor = ArgumentCaptor.forClass(Configuration.class);
        verify(framework).setConfiguration(captor.capture());

        Configuration configuration = captor.getValue();
        assertThat(configuration.getAllValues()).containsExactly(entry("foo", "1"));
    }

}
