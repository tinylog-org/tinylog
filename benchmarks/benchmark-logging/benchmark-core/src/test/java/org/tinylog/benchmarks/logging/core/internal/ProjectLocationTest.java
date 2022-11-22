package org.tinylog.benchmarks.logging.core.internal;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ProjectLocationTest {

    /**
     * Test for valid local Maven projects.
     */
    @Nested
    class SupportedLocalLocations {

        private ProjectLocation location;

        /**
         * Initializes the {@link ProjectLocation} with the current Maven project.
         */
        @BeforeEach
        void init() throws URISyntaxException {
            location = new ProjectLocation(ProjectLocationTest.class);
        }

        /**
         * Verifies that the base path will be resolved correctly.
         */
        @Test
        void getBasePath() {
            Path path = location.getBasePath();
            assertThat(path).isDirectory();
            assertThat(path.resolve("pom.xml")).isRegularFile();
        }

        /**
         * Verifies that the parent path will be resolved correctly.
         */
        @Test
        void getParentPath() {
            Path path = location.getParentPath();
            assertThat(path).isDirectory().isEqualTo(location.getBasePath().getParent());
        }

        /**
         * Verifies that a file will be resolved correctly.
         */
        @Test
        void resolveFile() {
            Path path = location.resolve("pom.xml");
            assertThat(path).isRegularFile().startsWith(location.getBasePath());
        }

        /**
         * Verifies that a nested path will be resolved correctly.
         */
        @Test
        void resolvePath() {
            Path path = location.resolve("src/main/java");
            assertThat(path).isDirectory().startsWith(location.getBasePath());
        }

    }

    /**
     * Test for unsupported class locations.
     */
    @Nested
    class UnsupportedThirdPartyLocations {

        /**
         * Verifies that classes of the JRE itself will be rejected.
         */
        @Test
        void classOfJre() {
            assertThatCode(() -> new ProjectLocation(String.class)).hasMessageContaining("java.lang.String");
        }

        /**
         * Verifies that classes of an external third-party JAR will be rejected.
         */
        @Test
        void classOfExternalJar() {
            assertThatCode(() -> new ProjectLocation(Logger.class)).hasMessageContaining("pom.xml");
        }

    }

}
