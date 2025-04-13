package org.tinylog.impl.path.segment;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = PathSegmentBuilder.class,
    implementations = {
        PathSegmentBuilderTest.FirstPathSegmentBuilder.class,
        PathSegmentBuilderTest.SecondPathSegmentBuilder.class
    }
)
class PathSegmentBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all path segment builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(PathSegmentBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstPathSegmentBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondPathSegmentBuilder.class));
    }

    /**
     * First path segment builder service implementation.
     */
    public static final class FirstPathSegmentBuilder implements PathSegmentBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public PathSegment create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second path segment builder service implementation.
     */
    public static final class SecondPathSegmentBuilder implements PathSegmentBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public PathSegment create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

}
