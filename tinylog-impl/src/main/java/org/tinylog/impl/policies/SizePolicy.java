package org.tinylog.impl.policies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Size policy that triggers a rollover event when a log file reaches a defined file size.
 */
public class SizePolicy implements Policy {

    private final long maxSize;
    private long currentSize;

    /**
     * @param maxSize The maximum size in bytes for log files
     */
    public SizePolicy(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean canContinueFile(Path file) throws IOException {
        try {
            return Files.size(file) < maxSize;
        } catch (NoSuchFileException ex) {
            return false;
        }
    }

    @Override
    public void init(Path file) throws IOException {
        try {
            currentSize = Files.size(file);
        } catch (NoSuchFileException ex) {
            currentSize = 0;
        }
    }

    @Override
    public boolean canAcceptLogEntry(int bytes) {
        currentSize += bytes;
        return currentSize <= maxSize;
    }

}
