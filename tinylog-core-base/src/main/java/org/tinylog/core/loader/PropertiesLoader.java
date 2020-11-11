package org.tinylog.core.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.tinylog.core.internal.InternalLogger;

/**
 * Configuration loader implementations for properties files.
 */
public class PropertiesLoader implements ConfigurationLoader {

	private static final Pattern URL_DETECTION_PATTERN = Pattern.compile("^[a-zA-Z]{2,}:/.*");

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
	public Map<Object, Object> load(ClassLoader classLoader) {
		String file = System.getProperty(CONFIGURATION_PROPERTY);

		if (file != null) {
			try (InputStream stream = getInputStream(classLoader, file)) {
				InternalLogger.info(null, "Load configuration from \"{}\"", file);
				Properties properties = new Properties();
				properties.load(stream);
				return properties;
			} catch (IOException ex) {
				InternalLogger.error(ex, "Failed to load tinylog configuration from \"{}\"", file);
			}
		}

		for (String name : CONFIGURATION_FILES) {
			try (InputStream stream = classLoader.getResourceAsStream(name)) {
				if (stream == null) {
					InternalLogger.debug(null, "Configuration file \"{}\" does not exist", name);
				} else {
					InternalLogger.info(null, "Load configuration from \"{}\"", name);
					Properties properties = new Properties();
					properties.load(stream);
					return properties;
				}
			} catch (IOException ex) {
				InternalLogger.error(ex, "Failed to load tinylog configuration from \"{}\"", name);
			}
		}

		return null;
	}

	/**
	 * Opens an URL, classpath resource, or local file as input stream.
	 *
	 * @param classLoader Class loader to use to open classpath resources
	 * @param file URL, classpath resource, or local file
	 * @return The input stream of the passed file
	 * @throws IOException Failed to open the passed file
	 */
	private InputStream getInputStream(ClassLoader classLoader, String file) throws IOException {
		if (URL_DETECTION_PATTERN.matcher(file).matches()) {
			return new URL(file).openStream();
		} else {
			InputStream stream = classLoader.getResourceAsStream(file);
			return stream == null ? new FileInputStream(file) : stream;
		}
	}

}
