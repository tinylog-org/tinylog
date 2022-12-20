package org.tinylog.impl.test;

import org.assertj.core.api.IterableAssert;
import org.tinylog.impl.backend.WriterRepository;
import org.tinylog.impl.writers.Writer;

/**
 * Custom assertions for testing tinylog data classes with AssertJ.
 */
public final class InternalAssertions {

    /** */
    private InternalAssertions() {
    }

    /**
     * Creates a new instance of {@link IterableAssert} for a {@link WriterRepository}.
     *
     * @param actual The actual writer repository to test
     * @return An iterable assert with all writers from the passed writer repository
     */
    public static IterableAssert<Writer> assertThat(WriterRepository actual) {
        return new IterableAssert<>(actual.getWriters());
    }

}
