package org.tinylog.impl.backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.writer.console.ConsoleWriter;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class BackendConfigurationParserTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that all severity levels are enabled and the default writer is created for an empty configuration.
     */
    @Tinylog(configuration = {})
    @Test
    void emptyConfiguration() {
        BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

        assertThat(configuration.getSeverityLevels())
            .hasSize(1)
            .anySatisfy((key, value) -> {
                assertThat(key).isEqualTo("");
                assertThat(value.getLevel("-")).isEqualTo(Level.TRACE);
                assertThat(value.getLevel("foo")).isEqualTo(Level.TRACE);
            });

        assertThat(configuration.getAllWriters()).singleElement().isInstanceOf(ConsoleWriter.class);
    }

    /**
     * Tests for global severity levels.
     */
    @Nested
    class SeverityLevels {

        /**
         * Verifies that a global custom severity level can be set.
         */
        @Tinylog(configuration = "level=INFO")
        @Test
        void globalLevel() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getSeverityLevels())
                .hasSize(1)
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("");
                    assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
                });
        }

        /**
         * Verifies that custom severity levels can be defined for specific static tags.
         */
        @Tinylog(configuration = "level=DEBUG@foo,WARN@bar")
        @Test
        void staticTags() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getSeverityLevels())
                .hasSize(1)
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("");
                    assertThat(value.getLevel("-")).isEqualTo(Level.OFF);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
                    assertThat(value.getLevel("bar")).isEqualTo(Level.WARN);
                });
        }

        /**
         * Verifies that custom severity levels can be defined for the untagged placeholder ({@code -}) and tagged
         * placeholder ({@code +}).
         */
        @Tinylog(configuration = "level=DEBUG@-,WARN@+")
        @Test
        void wildcardTags() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getSeverityLevels())
                .hasSize(1)
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("");
                    assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.WARN);
                    assertThat(value.getLevel("bar")).isEqualTo(Level.WARN);
                });
        }

        /**
         * Verifies that a custom severity level can be defined for the any placeholder ({@code *}) and overridden for
         * a specific static tag.
         */
        @Tinylog(configuration = "level=INFO@*,DEBUG@foo")
        @Test
        void overriddenAnyTag() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getSeverityLevels())
                .hasSize(1)
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("");
                    assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
                    assertThat(value.getLevel("bar")).isEqualTo(Level.INFO);
                });
        }

        /**
         * Verifies that custom severity levels can be defined for specific packages and classes.
         */
        @Tinylog(configuration = {
            "level@com.example=INFO",
            "level@com.example.MyClass=DEBUG",
            "level@org.example=WARN"
        })
        @Test
        void customPackagesAndClasses() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getSeverityLevels())
                .hasSize(4)
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("");
                    assertThat(value.getLevel("-")).isEqualTo(Level.TRACE);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.TRACE);
                })
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("com.example");
                    assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
                })
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("com.example.MyClass");
                    assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
                })
                .anySatisfy((key, value) -> {
                    assertThat(key).isEqualTo("org.example");
                    assertThat(value.getLevel("-")).isEqualTo(Level.WARN);
                    assertThat(value.getLevel("foo")).isEqualTo(Level.WARN);
                });
        }

    }

    /**
     * Tests for writers.
     */
    @Nested
    class Writers {

        /**
         * Verifies that a single writer can be defined.
         */
        @Tinylog(configuration = "writer.type=console")
        @Test
        void singleWriter() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getAllWriters()).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters(Level.TRACE)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("-", Level.TRACE)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("-", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("foo", Level.TRACE)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);
        }

        /**
         * Verifies that multiple writers with different severity levels can be defined.
         */
        @Tinylog(configuration = {
            "writer1.type=console",
            "writer1.level=DEBUG",
            "writer2.type=console",
            "writer2.level=WARN"
        })
        @Test
        void multipleWriters() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getAllWriters())
                .hasSize(2)
                .hasOnlyElementsOfTypes(ConsoleWriter.class)
                .doesNotHaveDuplicates();

            assertThat(configuration.getWriters(Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters(Level.DEBUG)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.INFO)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.WARN)).hasSize(2).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.ERROR)).hasSize(2).doesNotHaveDuplicates();

            assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("-", Level.WARN)).hasSize(2).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(2).doesNotHaveDuplicates();

            assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(2).doesNotHaveDuplicates();
            assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(2).doesNotHaveDuplicates();
        }

        /**
         * Verifies that the most severe level will be used, if there is a global severity level and a writer specific
         * severity level.
         */
        @Tinylog(configuration = {"level=INFO", "writer.type=console", "writer.level=DEBUG"})
        @Test
        void inheritSeverityLevel() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getAllWriters()).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters(Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters(Level.DEBUG)).isEmpty();
            assertThat(configuration.getWriters(Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("-", Level.DEBUG)).isEmpty();
            assertThat(configuration.getWriters("-", Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("-", Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("-", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("foo", Level.DEBUG)).isEmpty();
            assertThat(configuration.getWriters("foo", Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);
        }

        /**
         * Verifies that writers can be enabled only for specific tags.
         */
        @Tinylog(configuration = {"writer.type=console", "writer.level=DEBUG@foo"})
        @Test
        void singleTag() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getAllWriters()).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters(Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters(Level.DEBUG)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters(Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("-", Level.ERROR)).isEmpty();

            assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("foo", Level.DEBUG)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);
        }

        /**
         * Verifies that writers can be enabled for distinct tags.
         */
        @Tinylog(configuration = {
            "writer1.type=console", "writer1.level=DEBUG@foo",
            "writer2.type=console", "writer2.level=WARN@bar"
        })
        @Test
        void multipleTags() {
            BackendConfiguration configuration = new BackendConfigurationParser(context).parse();

            assertThat(configuration.getAllWriters()).hasSize(2).doesNotHaveDuplicates();

            assertThat(configuration.getWriters(Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters(Level.DEBUG)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.INFO)).hasSize(1).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.WARN)).hasSize(2).doesNotHaveDuplicates();
            assertThat(configuration.getWriters(Level.ERROR)).hasSize(2).doesNotHaveDuplicates();

            assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("-", Level.ERROR)).isEmpty();

            assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("foo", Level.DEBUG)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.INFO)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("foo", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);

            assertThat(configuration.getWriters("bar", Level.TRACE)).isEmpty();
            assertThat(configuration.getWriters("bar", Level.DEBUG)).isEmpty();
            assertThat(configuration.getWriters("bar", Level.INFO)).isEmpty();
            assertThat(configuration.getWriters("bar", Level.WARN)).singleElement().isInstanceOf(ConsoleWriter.class);
            assertThat(configuration.getWriters("bar", Level.ERROR)).singleElement().isInstanceOf(ConsoleWriter.class);
        }

    }

}
