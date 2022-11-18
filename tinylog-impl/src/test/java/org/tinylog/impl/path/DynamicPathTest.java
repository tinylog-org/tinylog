package org.tinylog.impl.path;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class DynamicPathTest {

    @TempDir
    private Path directory;

    @Inject
    private Framework framework;

    /**
     * Tests for {@link  DynamicPath#getLatestPath()}.
     */
    @Nested
    class FindingLatestPath {

        /**
         * Verifies that no latest path is resolved for an empty dynamic path.
         */
        @Test
        void findEmptyPath() throws Exception {
            DynamicPath path = new DynamicPath(framework, "");
            assertThat(path.getLatestPath()).isNull();
        }

        /**
         * Verifies that no latest path is resolved for a dynamic path whose parent directory doesn't exist.
         */
        @Test
        void findNonExisingPath() throws Exception {
            DynamicPath path = new DynamicPath(framework, directory + "/foo/file_{count}.log");
            assertThat(path.getLatestPath()).isNull();
        }

        /**
         * Verifies that an existing file is resolved for a static dynamic path without placeholders.
         */
        @Test
        void findExistingPathWithoutPlaceholders() throws Exception {
            Path file = directory.resolve("foo");
            Files.createFile(file);

            DynamicPath path = new DynamicPath(framework, file.toString());
            assertThat(path.getLatestPath()).isEqualTo(file);
        }

        /**
         * Verifies that no latest path is resolved for a static dynamic path without placeholders, if the file does
         * not exist.
         */
        @Test
        void findNonExistentPathWithoutPlaceholders() throws Exception {
            Path file = directory.resolve("foo");

            DynamicPath path = new DynamicPath(framework, file.toString());
            assertThat(path.getLatestPath()).isNull();
        }

        /**
         * Verifies that the latest existing file is resolved for a dynamic path with placeholders.
         */
        @Test
        void findExistingPathWithPlaceholders() throws Exception {
            Path oldestFile = directory.resolve("foo0").resolve("file_42.log");
            Files.createDirectories(oldestFile.getParent());
            Files.createFile(oldestFile);

            Path youngestFile = directory.resolve("foo42").resolve("file_0.log");
            Files.createDirectories(youngestFile.getParent());
            Files.createFile(youngestFile);

            DynamicPath path = new DynamicPath(framework, directory + "/foo{count}/file_{count}.log");
            assertThat(path.getLatestPath()).isEqualTo(youngestFile);
        }

        /**
         * Verifies that no latest path is resolved for a dynamic path with placeholders, if no matching file exists.
         */
        @Test
        void findNonExistentPathWithPlaceholders() throws Exception {
            Files.createDirectories(directory.resolve("foo42"));

            DynamicPath path = new DynamicPath(framework, directory + "/foo{count}/file_{count}.log");
            assertThat(path.getLatestPath()).isNull();
        }

    }

    /**
     * Tests for {@link  DynamicPath#generateNewPath()}.
     */
    @Nested
    class GeneratingNewPath {

        /**
         * Verifies that a file path with dynamic placeholders can be generated.
         */
        @Test
        void generateDynamicPath() throws Exception {
            Clock clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
            Framework epochFramework = spy(framework);
            when(epochFramework.getClock()).thenReturn(clock);

            DynamicPath path = new DynamicPath(epochFramework, directory + "/{date: yyyy-MM-dd}.log");
            assertThat(path.generateNewPath()).isEqualTo(directory.resolve("1970-01-01.log"));
        }

        /**
         * Verifies that the directory for the generated file will be created.
         */
        @Test
        void createDirectories() throws Exception {
            Path file = directory.resolve("sub").resolve("folder").resolve("foo.log");
            DynamicPath path = new DynamicPath(framework, file.toString());
            assertThat(path.generateNewPath())
                .isEqualTo(file)
                .doesNotExist()
                .extracting(Path::getParent, as(InstanceOfAssertFactories.PATH)).isDirectory();
        }

    }

}
