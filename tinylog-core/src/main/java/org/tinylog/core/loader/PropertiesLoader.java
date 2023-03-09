package org.tinylog.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

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
    public Map<String, String> load(ClassLoader loader) {
        String file = System.getProperty(CONFIGURATION_PROPERTY);

        if (file != null) {
            try (InputStream stream = getInputStream(loader, file)) {
                InternalLogger.info(null, "Load configuration from \"{}\"", file);
                return load(loader, stream);
            } catch (IOException ex) {
                InternalLogger.error(ex, "Failed to load tinylog configuration from \"{}\"", file);
            }
        }

        for (String name : CONFIGURATION_FILES) {
            try (InputStream stream = loader.getResourceAsStream(name)) {
                if (stream == null) {
                    InternalLogger.debug(null, "Configuration file \"{}\" does not exist", name);
                } else {
                    InternalLogger.info(null, "Load configuration from \"{}\"", name);
                    return load(loader, stream);
                }
            } catch (IOException ex) {
                InternalLogger.error(ex, "Failed to load tinylog configuration from \"{}\"", name);
            }
        }

        return null;
    }

    /**
     * Loads the properties from an input stream and resolves all variables.
     *
     * @param loader The class loader to use for loading the service files and service implementation classes
     * @param stream The input stream of a properties file
     * @return All properties as map
     * @throws IOException Failed to read from the passed input stream
     */
    private Map<String, String> load(ClassLoader loader, InputStream stream) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();

        new Properties() {
            @Override
            public Object put(Object key, Object value) {
                return map.put((String) key, (String) value);
            }
        }.load(stream);

        resolveVariables(loader, map);

        return map;
    }

}