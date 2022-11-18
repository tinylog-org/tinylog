package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.stream.Collectors;

import org.tinylog.core.Framework;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

/**
 * Builder for creating an instance of {@link NativeLoggingBackend}.
 */
public class NativeLoggingBackendBuilder implements LoggingBackendBuilder {

    private static final int QUEUE_SIZE = 64 * 1024;

    /** */
    public NativeLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "tinylog";
    }

    @Override
    public LoggingBackend create(Framework framework) {
        LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();
        WritingThread writingThread = createWritingThread(configuration.getAllWriters());
        return new NativeLoggingBackend(framework, configuration, writingThread);
    }

    /**
     * Creates a {@link WritingThread} instance if there is at least one {@link AsyncWriter}.
     *
     * @param writers All sync and async writers
     * @return A started instance of {@link WritingThread} if there is at least one {@link AsyncWriter} in the passed
     *         writer collection, otherwise {@code null}
     */
    private static WritingThread createWritingThread(Collection<Writer> writers) {
        Collection<AsyncWriter> asyncWriters = writers.stream()
            .filter(writer -> writer instanceof AsyncWriter)
            .map(writer -> (AsyncWriter) writer)
            .collect(Collectors.toList());

        return asyncWriters.isEmpty() ? null : new WritingThread(asyncWriters, QUEUE_SIZE);
    }

}
