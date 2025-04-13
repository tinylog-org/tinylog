package org.tinylog.impl.writer;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = WriterBuilder.class,
    implementations = {
        WriterBuilderTest.FirstWriterBuilder.class,
        WriterBuilderTest.SecondWriterBuilder.class
    }
)
class WriterBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all writer builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(WriterBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstWriterBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondWriterBuilder.class));
    }

    /**
     * First writer builder service implementation.
     */
    public static final class FirstWriterBuilder implements WriterBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public Writer create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second writer builder service implementation.
     */
    public static final class SecondWriterBuilder implements WriterBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public Writer create(TinylogContext context) {
            throw new UnsupportedOperationException();
        }

    }

}
