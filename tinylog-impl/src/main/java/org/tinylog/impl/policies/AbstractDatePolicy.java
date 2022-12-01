package org.tinylog.impl.policies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Abstract policy for policies that are based on dates and times.
 */
public abstract class AbstractDatePolicy implements Policy {

    private final Clock clock;
    private final ZoneId zone;
    private Instant deadline;

    /**
     * @param clock The clock for receiving the current date and time
     * @param zone The time zone to sue for date and time
     */
    public AbstractDatePolicy(Clock clock, ZoneId zone) {
        this.clock = clock;
        this.zone = zone;
    }

    @Override
    public boolean canContinueFile(Path file) throws IOException {
        ZonedDateTime now = ZonedDateTime.ofInstant(clock.instant(), zone);
        ZonedDateTime minDate = getMinDate(now);
        FileTime creationTime = Files.readAttributes(file, BasicFileAttributes.class).creationTime();
        return !creationTime.toInstant().isBefore(minDate.toInstant());
    }

    @Override
    public void init(Path file) {
        ZonedDateTime now = ZonedDateTime.ofInstant(clock.instant(), zone);
        ZonedDateTime date = getMaxDate(now);
        deadline = date.toInstant();
    }

    @Override
    public boolean canAcceptLogEntry(int bytes) {
        return clock.instant().isBefore(deadline);
    }

    /**
     * Gets the minimum {@link ZonedDateTime} that is accepted as creation date-time for log files to continue them.
     *
     * @param now The current date-time
     * @return The minimum acceptable creation date-time
     */
    protected abstract ZonedDateTime getMinDate(ZonedDateTime now);

    /**
     * Gets the maximum {@link ZonedDateTime} until new log entries can be written to the current log file.
     *
     * @param now The current date-time
     * @return The deadline for accepting log entries for the current log file
     */
    protected abstract ZonedDateTime getMaxDate(ZonedDateTime now);

}
