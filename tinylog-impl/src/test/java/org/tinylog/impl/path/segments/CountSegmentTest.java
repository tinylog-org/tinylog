package org.tinylog.impl.path.segments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class CountSegmentTest {

    @TempDir
    private Path folder;

    /**
     * Tests for {@link CountSegment#findLatest(Path, String)}.
     */
    @Nested
    class FindingLatest {

        /**
         * Verifies that null is returned if there are no matching files.
         */
        @Test
        void noFiles() throws IOException {
            Files.createFile(folder.resolve("fooBAR.log"));
            Files.createFile(folder.resolve("bar0.log"));

            String latest = new CountSegment().findLatest(folder, "foo");
            assertThat(latest).isNull();
        }

        /**
         * Verifies that the count of the only matching file is used.
         */
        @Test
        void oneFile() throws IOException {
            Files.createFile(folder.resolve("foo0.log"));

            String latest = new CountSegment().findLatest(folder, "foo");
            assertThat(latest).isEqualTo("0");
        }

        /**
         * Verifies that the largest count of multiple matching files is used.
         */
        @Test
        void multipleFiles() throws IOException {
            Files.createFile(folder.resolve("foo8.log"));
            Files.createFile(folder.resolve("foo30.log"));
            Files.createFile(folder.resolve("foo42.log"));

            String latest = new CountSegment().findLatest(folder, "foo");
            assertThat(latest).isEqualTo("42");
        }

    }

    /**
     * Tests for {@link CountSegment#resolve(StringBuilder, ZonedDateTime)}.
     */
    @Nested
    class Resolving {

        /**
         * Verifies that the count starts with "0" if no predecessor exists.
         */
        @Test
        void firstCount() throws IOException {
            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar0").toString());
        }

        /**
         * Verifies that the count resumes with "1" if a predecessor exists with "0".
         */
        @Test
        void secondCount() throws IOException {
            Files.createFile(folder.resolve("bar0"));

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar1").toString());
        }

        /**
         * Verifies that the count resumes with "2" if predecessors exist with "0" and "1".
         */
        @Test
        void thirdCount() throws IOException {
            Files.createFile(folder.resolve("bar0"));
            Files.createFile(folder.resolve("bar1"));

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar2").toString());
        }

        /**
         * Verifies that the count resumes with "9223372036854775807" if a predecessor exists with
         * "9223372036854775806".
         */
        @Test
        void secondToLastCount() throws IOException {
            Files.createFile(folder.resolve("bar" + (Long.MAX_VALUE - 1)));

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar").toString() + Long.MAX_VALUE);
        }

        /**
         * Verifies that the count resumes with "0" if a predecessor exists with "9223372036854775807".
         */
        @Test
        void lastCount() throws IOException {
            Files.createFile(folder.resolve("bar" + Long.MAX_VALUE));

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar0").toString());
        }

        /**
         * Verifies that the count works without any file prefix.
         */
        @Test
        void rootCount() throws IOException {
            Files.createFile(folder.resolve("0"));

            StringBuilder builder = new StringBuilder(folder + folder.getFileSystem().getSeparator());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("1").toString());
        }

        /**
         * Verifies that the count works even if the directory does not exist.
         */
        @Test
        void missingDirectory() throws IOException {
            Files.delete(folder);

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar0").toString());
        }

        /**
         * Verifies that files without any valid numeric count are ignored.
         */
        @Test
        void invalidCount() throws IOException {
            Files.createFile(folder.resolve("barFOO"));

            StringBuilder builder = new StringBuilder(folder.resolve("bar").toString());
            new CountSegment().resolve(builder, null);
            assertThat(builder).asString().isEqualTo(folder.resolve("bar0").toString());
        }

    }

}
