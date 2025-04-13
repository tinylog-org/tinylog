package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = PlaceholderBuilder.class,
    implementations = {
        PlaceholderBuilderTest.FirstPlaceholderBuilder.class,
        PlaceholderBuilderTest.SecondPlaceholderBuilder.class
    }
)
class PlaceholderBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all placeholder builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(PlaceholderBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstPlaceholderBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondPlaceholderBuilder.class));
    }

    /**
     * First placeholder builder service implementation.
     */
    public static final class FirstPlaceholderBuilder implements PlaceholderBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public Placeholder create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second placeholder builder service implementation.
     */
    public static final class SecondPlaceholderBuilder implements PlaceholderBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public Placeholder create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

}
