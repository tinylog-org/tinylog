package org.tinylog.impl.backend;

import java.util.Collection;

import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.writers.Writer;

/**
 * Builder for creating an instance of {@link ImmutableLoggingBackend}.
 */
public class TinylogLoggingBackendBuilder implements LoggingBackendBuilder {

    private static final int QUEUE_SIZE = 64 * 1024;

    /** */
    public TinylogLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "tinylog";
    }

    @Override
    public LoggingBackend create(LoggingContext context) {
        LoggingConfiguration configuration = new LoggingConfigurationParser(context).parse();
        WritingThread writingThread = createWritingThread(configuration.getAllWriters());
        return new ImmutableLoggingBackend(context, configuration, writingThread);
    }

    /**
     * Creates a {@link WritingThread} instance if there is at least one {@link Writer}.
     *
     * @param writers All writers
     * @return A started instance of {@link WritingThread} if there is at least one {@link Writer} in the passed
     *         writer collection, otherwise {@code null}
     */
    private static WritingThread createWritingThread(Collection<Writer> writers) {
        return writers.isEmpty() ? null : new WritingThread(writers, QUEUE_SIZE);
    }

}
