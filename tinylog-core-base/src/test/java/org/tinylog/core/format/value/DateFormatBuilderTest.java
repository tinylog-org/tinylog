package org.tinylog.core.format.value;

import java.util.Locale;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateFormatBuilderTest {

    /**
     * Verifies that the builder can create an instance of {@link DateFormat}.
     */
    @Test
    void creation() {
        DateFormatBuilder builder = new DateFormatBuilder();
        assertThat(builder.create(Locale.GERMANY)).isInstanceOf(DateFormat.class);
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(ValueFormatBuilder.class))
            .anyMatch(builder -> builder instanceof DateFormatBuilder);
    }

}
