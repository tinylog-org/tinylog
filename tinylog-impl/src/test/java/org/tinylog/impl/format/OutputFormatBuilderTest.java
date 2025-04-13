package org.tinylog.impl.format;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = OutputFormatBuilder.class,
    implementations = {
        OutputFormatBuilderTest.FirstOutputFormatBuilder.class,
        OutputFormatBuilderTest.SecondOutputFormatBuilder.class
    }
)
class OutputFormatBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all output format builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(OutputFormatBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstOutputFormatBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondOutputFormatBuilder.class));
    }

    /**
     * First output format builder service implementation.
     */
    public static final class FirstOutputFormatBuilder implements OutputFormatBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public OutputFormat create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second output format builder service implementation.
     */
    public static final class SecondOutputFormatBuilder implements OutputFormatBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public OutputFormat create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

}
