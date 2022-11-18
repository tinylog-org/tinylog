package org.tinylog.slf4j;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class TinylogLoggerFactoryTest {

    @Inject
    private Framework framework;

    /**
     * Verifies that the factory provides the same logger instance for the same name.
     */
    @Test
    void getSameLoggerInstanceForSameName() {
        TinylogLoggerFactory factory = new TinylogLoggerFactory(framework);
        TinylogLogger logger = factory.getLogger("Foo");
        TinylogLogger other = factory.getLogger("Foo");
        assertThat(other).isSameAs(logger);
    }

    /**
     * Verifies that the factory provides another same logger instance for another name.
     */
    @Test
    void getDifferentLoggerInstanceForDifferentName() {
        TinylogLoggerFactory factory = new TinylogLoggerFactory(framework);
        TinylogLogger logger = factory.getLogger("Foo");
        TinylogLogger other = factory.getLogger("Bar");
        assertThat(other).isNotSameAs(logger);
    }

}
