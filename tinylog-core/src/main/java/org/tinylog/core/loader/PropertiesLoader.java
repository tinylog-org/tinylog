package org.tinylog.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Configuration loader implementations for properties files.
 */
public class PropertiesLoader extends AbstractConfigurationLoader {

    private static final String CONFIGURATION_PROPERTY = "tinylog.configuration";

    private static final String[] CONFIGURATION_FILES = new String[] {
        "tinylog-dev.properties",
        "tinylog-test.properties",
        "tinylog.properties",
    };

    /** */
    public PropertiesLoader() {
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Map<String, String> load(ClassLoader loader, InternalLogger logger) {
        String file = System.getProperty(CONFIGURATION_PROPERTY);

        if (file != null) {
            try (InputStream stream = getInputStream(loader, file)) {
                logger.log(Level.INFO, "Load configuration from \"{}\"", file);
                return load(loader, logger, stream);
            } catch (IOException ex) {
                logger.log(Level.ERROR, ex, "Failed to load tinylog configuration from \"{}\"", file);
            }
        }

        for (String name : CONFIGURATION_FILES) {
            try (InputStream stream = loader.getResourceAsStream(name)) {
                if (stream == null) {
                    logger.log(Level.DEBUG, "Configuration file \"{}\" does not exist", name);
                } else {
                    logger.log(Level.INFO, "Load configuration from \"{}\"", name);
                    return load(loader, logger, stream);
                }
            } catch (IOException ex) {
                logger.log(Level.ERROR, ex, "Failed to load tinylog configuration from \"{}\"", name);
            }
        }

        return null;
    }

    /**
     * Loads the properties from an input stream and resolves all variables.
     *
     * @param loader The class loader to use for loading the service files and service implementation classes
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @param stream The input stream of a properties file
     * @return All properties as map
     * @throws IOException If failed to read from the passed input stream
     */
    private Map<String, String> load(
        ClassLoader loader,
        InternalLogger logger,
        InputStream stream
    ) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();

        new Properties() {
            @Override
            public Object put(Object key, Object value) {
                return map.put((String) key, (String) value);
            }
        }.load(stream);

        resolveVariables(loader, logger, map);

        return map;
    }

}
