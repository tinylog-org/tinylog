package org.tinylog.impl.policy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.test.junit.log.TestClock;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class MonthlyPolicyTest {

    @Inject
    private TestClock clock;

    /**
     * Tests for {@link MonthlyPolicy#canContinueFile(Path)}.
     */
    @Nested
    class CanContinueFile {

        @TempDir
        private Path directory;
        private Path file;

        /**
         * Creates a temporary log file.
         */
        @BeforeEach
        void init() throws IOException {
            file = Files.createTempFile(directory, "tinylog", ".log");
        }

        /**
         * Verifies that a log file created on the last rollover time can be continued.
         */
        @Test
        void eldestContinuableFile() throws IOException {
            clock.fixTo(Instant.parse("2000-01-01T02:59:59Z"));
            applyFileTime(file, Instant.parse("1999-12-01T03:00:00Z"));

            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));
            assertThat(policy.canContinueFile(file)).isTrue();
        }

        /**
         * Verifies that a log file created before the last rollover time cannot be continued.
         */
        @Test
        void youngestNotContinuableFile() throws IOException {
            clock.fixTo(Instant.parse("2000-01-01T02:59:59Z"));
            applyFileTime(file, Instant.parse("1999-12-01T02:59:59Z"));

            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));
            assertThat(policy.canContinueFile(file)).isFalse();
        }

        /**
         * Verifies that a log file created on the current rollover time can be continued.
         */
        @Test
        void sameDateTime() throws IOException {
            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));
            applyFileTime(file, Instant.parse("2000-01-01T03:00:00Z"));

            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));
            assertThat(policy.canContinueFile(file)).isTrue();
        }

        /**
         * Verifies that a log file created in the future can be continued.
         */
        @Test
        void futureDateTime() throws IOException {
            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));
            applyFileTime(file, Instant.parse("2100-12-01T23:59:59Z"));

            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));
            assertThat(policy.canContinueFile(file)).isTrue();
        }

        /**
         * Sets the passed instant as create time and last modified time for the passed file.
         *
         * @param file The file to update
         * @param instant The creation time and last modified time
         * @throws IOException If failed to update create time or last modified time
         */
        void applyFileTime(Path file, Instant instant) throws IOException {
            FileTime fileTime = FileTime.from(instant);
            Files.getFileAttributeView(file, BasicFileAttributeView.class).setTimes(fileTime, null, fileTime);
        }

    }

    /**
     * Tests for {@link MonthlyPolicy#canAcceptDataRecord(int)}.
     */
    @Nested
    class CanAcceptDataRecord {

        /**
         * Verifies that log entries are accepted if the current date-time hasn't been changed since the initialization.
         */
        @Test
        void firstAcceptedLogEntry() {
            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));

            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));
            policy.init(null);
            assertThat(policy.canAcceptDataRecord(0)).isTrue();

        }

        /**
         * Verifies that log entries are accepted until reaching the date-time for the next rollover event.
         */
        @Test
        void latestAcceptedLogEntry() {
            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));

            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));
            policy.init(null);

            clock.fixTo(Instant.parse("2000-01-31T02:59:59Z"));
            assertThat(policy.canAcceptDataRecord(0)).isTrue();
        }

        /**
         * Verifies that log entries are not accepted anymore when reaching the date-time for the next rollover event.
         */
        @Test
        void earliestUnacceptedLogEntry() {
            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));

            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));
            policy.init(null);

            clock.fixTo(Instant.parse("2000-02-01T03:00:00Z"));
            assertThat(policy.canAcceptDataRecord(0)).isFalse();
        }

        /**
         * Verifies that log entries from the past are accepted.
         */
        @Test
        void pastDateTime() {
            MonthlyPolicy policy = new MonthlyPolicy(clock, ZoneOffset.UTC, LocalTime.of(3, 0));

            clock.fixTo(Instant.parse("2000-01-01T03:00:00Z"));
            policy.init(null);

            clock.fixTo(Instant.parse("1999-12-31T03:00:00Z"));
            assertThat(policy.canAcceptDataRecord(0)).isTrue();
        }

    }

}
