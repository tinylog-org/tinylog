package org.tinylog.core.runtime;

import java.time.Duration;
import java.util.regex.Pattern;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Abstract runtime implementation with OS detection.
 */
public abstract class AbstractRuntime implements RuntimeFlavor {

    private static final Pattern OS_NAME_WITH_VERSION = Pattern.compile("^.* \\d+$");

    private final long startTime;

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public AbstractRuntime(InternalLogger logger) {
        startTime = System.nanoTime();

        logger.log(Level.DEBUG, "Operating system: {}", getOperatingSystem());
        logger.log(Level.DEBUG, "Virtual machine: {}", getVirtualMachine());
    }

    @Override
    public String getOperatingSystem() {
        String name = System.getProperty("os.name");
        String version = System.getProperty("os.version");

        if (OS_NAME_WITH_VERSION.matcher(name).matches()) {
            return name;
        } else {
            return name + " " + version;
        }
    }

    @Override
    public Duration getUptime() {
        return Duration.ofNanos(System.nanoTime() - startTime);
    }

}
