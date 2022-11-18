package org.tinylog.impl.policies;

import java.nio.file.Path;

/**
 * Startup policy that triggers a rollover event when trying to continue an already existing log file at application
 * startup.
 */
public class StartupPolicy implements Policy {

    /** */
    public StartupPolicy() {
    }

    @Override
    public boolean canContinueFile(Path file) {
        return false;
    }

    @Override
    public void init(Path file) {
        // Ignore
    }

    @Override
    public boolean canAcceptLogEntry(int bytes) {
        return true;
    }

}
