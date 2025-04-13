package org.tinylog.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.RestoreSystemProperties;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tinylog
@RestoreSystemProperties
class PropertiesLoaderTest {

    @TempDir
    private Path folder;

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    private URLClassLoader classLoader;

    /**
     * Creates a class loader that contains the current temporary folder.
     */
    @BeforeEach
    void init() throws MalformedURLException {
        URL[] urls = new URL[] {folder.toUri().toURL()};
        classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Closes the crated URL class loader.
     */
    @AfterEach
    void destroy() throws IOException {
        classLoader.close();
    }

    /**
     * Verifies that the loader is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(ConfigurationLoader.class))
            .anyMatch(loader -> loader instanceof PropertiesLoader);
    }

    /**
     * Verifies that the priority is "0".
     */
    @Test
    void priority() {
        PropertiesLoader loader = new PropertiesLoader();
        assertThat(loader.getPriority()).isZero();
    }

    /**
     * Verifies that {@code tinylog.properties} will be loaded, if there is no other properties file.
     */
    @Test
    void loadDefaultProductionPropertiesFile() throws IOException {
        createTextFile("tinylog.properties", "environment = production");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("environment", "production"));
    }

    /**
     * Verifies that {@code tinylog-test.properties} will be loaded, if there is no other properties file.
     */
    @Test
    void loadDefaultTestPropertiesFile() throws IOException {
        createTextFile("tinylog-test.properties", "environment = test");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("environment", "test"));
    }

    /**
     * Verifies that {@code tinylog-dev.properties} will be loaded, if there is no other properties file.
     */
    @Test
    void loadDefaultDevelopmentPropertiesFile() throws IOException {
        createTextFile("tinylog-dev.properties", "environment = development");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("environment", "development"));
    }

    /**
     * Verifies that {@code tinylog-test.properties} will be loaded, if {@code tinylog.properties} and
     * {@code tinylog-test.properties} are available.
     */
    @Test
    void preferTestOverProductionPropertiesFile() throws IOException {
        createTextFile("tinylog.properties", "production = yes");
        createTextFile("tinylog-test.properties", "test = yes");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("test", "yes"));
    }

    /**
     * Verifies that {@code tinylog-dev.properties} will be loaded, if {@code tinylog-test.properties} and
     * {@code tinylog-dev.properties} are available.
     */
    @Test
    void preferDevelopmentOverTestPropertiesFile() throws IOException {
        createTextFile("tinylog-test.properties", "test = yes");
        createTextFile("tinylog-dev.properties", "development = yes");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("development", "yes"));
    }

    /**
     * Verifies that a custom resource from the classpath can be provided as tinylog configuration.
     */
    @Test
    void loadCustomResource() throws IOException {
        createTextFile("my-configuration.properties", "foo = bar");
        System.setProperty("tinylog.configuration", "my-configuration.properties");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("foo", "bar"));
    }

    /**
     * Verifies that a custom local file can be provided as tinylog configuration.
     */
    @Test
    void loadCustomLocalFile() throws IOException {
        Path file = createTextFile("my-configuration.properties", "foo = bar");
        System.setProperty("tinylog.configuration", file.toString());

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("foo", "bar"));
    }

    /**
     * Verifies that a custom URL can be provided as tinylog configuration.
     */
    @Test
    void loadCustomUrl() throws IOException {
        Path file = createTextFile("my-configuration.properties", "foo = bar");
        System.setProperty("tinylog.configuration", file.toUri().toURL().toString());

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("foo", "bar"));
    }

    /**
     * Verifies that no default properties files will be loaded, if a custom configuration is provided.
     */
    @Test
    void preferCustomOverDefaultPropertiesFile() throws IOException {
        createTextFile("tinylog-custom.properties", "custom = yes");
        createTextFile("tinylog.properties", "production = yes");
        createTextFile("tinylog-test.properties", "test = yes");
        createTextFile("tinylog-dev.properties", "development = yes");

        System.setProperty("tinylog.configuration", "tinylog-custom.properties");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("custom", "yes"));
    }

    /**
     * Verifies that an error will be output and configuration will be loaded from an available classpath resource,
     * if the defined custom configuration does not exist.
     */
    @Test
    void printErrorIfCustomPropertiesFileDoesNotExist() throws IOException {
        createTextFile("tinylog.properties", "production = yes");
        System.setProperty("tinylog.configuration", "tinylog-custom.properties");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("production", "yes"));

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("tinylog-custom.properties");
        });
    }

    /**
     * Verifies that an error message will be output, if a resource stream throws an {@link IOException}.
     */
    @Test
    void printErrorIfLoadingPropertiesFileFails() throws IOException {
        URLClassLoader classLoader = new URLClassLoader(new URL[0], this.classLoader) {
            @Override
            public InputStream getResourceAsStream(String name) {
                try {
                    InputStream stream = mock(InputStream.class);
                    when(stream.read(any(byte[].class))).thenThrow(new IOException("Invalid resource"));
                    return stream;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        try (classLoader) {
            new PropertiesLoader().load(classLoader, logger);

            assertThat(log.consume()).hasSize(3).allSatisfy(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getThrowable()).hasMessage("Invalid resource");
            });
        }
    }

    /**
     * Verifies that the original order of all properties is preserved.
     */
    @Test
    void preserveOrderOfProperties() throws IOException {
        createTextFile("tinylog.properties", "b=1", "c=2", "a=3");

        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);
        assertThat(properties).containsExactly(entry("b", "1"), entry("c", "2"), entry("a", "3"));
    }

    /**
     * Verifies that a system property without any default value can be resolved.
     */
    @Test
    void resolveExistingSystemPropertyWithoutDefault() throws IOException {
        createTextFile("tinylog.properties", "example = #{foo}");

        System.setProperty("foo", "42");
        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);

        assertThat(properties).containsExactly(entry("example", "42"));
    }

    /**
     * Verifies that a system property without any default value will be kept unchanged, if it cannot be resolved.
     */
    @Test
    void resolveMissingSystemPropertyWithoutDefault() throws IOException {
        createTextFile("tinylog.properties", "example = #{foo}");

        System.clearProperty("foo");
        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);

        assertThat(properties).containsExactly(entry("example", "#{foo}"));
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

    /**
     * Verifies that a system property with a defined default value can be resolved.
     */
    @Test
    void resolveExistingSystemPropertyWithDefault() throws IOException {
        createTextFile("tinylog.properties", "example = #{ foo | default }");

        System.setProperty("foo", "42");
        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);

        assertThat(properties).containsExactly(entry("example", "42"));
    }

    /**
     * Verifies that the default value of a system property with a defined default value will be used, if the system
     * property cannot be resolved.
     */
    @Test
    void resolveMissingSystemPropertyWithDefault() throws IOException {
        createTextFile("tinylog.properties", "example = #{ foo | default }");

        System.clearProperty("foo");
        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);

        assertThat(properties).containsExactly(entry("example", "default"));
    }

    /**
     * Verifies that multiple system properties can be resolved.
     */
    @Test
    void resolveMultipleSystemProperties() throws IOException {
        createTextFile("tinylog.properties", "example = <#{foo}> <#{bar}>");

        System.setProperty("foo", "1");
        System.setProperty("bar", "2");
        Map<String, String> properties = new PropertiesLoader().load(classLoader, logger);

        assertThat(properties).containsExactly(entry("example", "<1> <2>"));
    }

    /**
     * Creates a text file in the current temporary folder.
     *
     * @param fileName File name for the text file
     * @param lines Lines to write to the text file
     * @return The created file
     */
    private Path createTextFile(String fileName, String... lines) throws IOException {
        Path file = folder.resolve(fileName);
        String content = String.join("\n", lines);
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

}
