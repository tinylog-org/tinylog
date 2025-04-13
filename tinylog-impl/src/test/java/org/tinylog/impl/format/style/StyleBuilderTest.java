package org.tinylog.impl.format.style;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = StyleBuilder.class,
    implementations = {
        StyleBuilderTest.FirstStyleBuilder.class,
        StyleBuilderTest.SecondStyleBuilder.class
    }
)
class StyleBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all style builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(StyleBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstStyleBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondStyleBuilder.class));
    }

    /**
     * First style builder service implementation.
     */
    public static final class FirstStyleBuilder implements StyleBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public Placeholder create(TinylogContext context, Placeholder placeholder, String value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second style builder service implementation.
     */
    public static final class SecondStyleBuilder implements StyleBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public Placeholder create(TinylogContext context, Placeholder placeholder, String value) {
            throw new UnsupportedOperationException();
        }

    }

}
