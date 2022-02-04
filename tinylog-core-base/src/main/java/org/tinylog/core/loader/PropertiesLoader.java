package org.tinylog.core.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.core.variable.VariableResolver;

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
	public String getName() {
		return "properties";
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public Map<String, String> load(Framework framework) {
		String file = System.getProperty(CONFIGURATION_PROPERTY);

		if (file != null) {
			try (InputStream stream = getInputStream(framework.getClassLoader(), file)) {
				InternalLogger.info(null, "Load configuration from \"{}\"", file);
				return load(framework, stream);
			} catch (IOException ex) {
				InternalLogger.error(ex, "Failed to load tinylog configuration from \"{}\"", file);
			}
		}

		for (String name : CONFIGURATION_FILES) {
			try (InputStream stream = framework.getClassLoader().getResourceAsStream(name)) {
				if (stream == null) {
					InternalLogger.debug(null, "Configuration file \"{}\" does not exist", name);
				} else {
					InternalLogger.info(null, "Load configuration from \"{}\"", name);
					return load(framework, stream);
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
	 * @param framework The actual framework instance
	 * @param stream The input stream of a properties file
	 * @return All properties as map
	 * @throws IOException Failed to read from the passed input stream
	 */
	private Map<String, String> load(Framework framework, InputStream stream) throws IOException {
		Map<String, String> map = new LinkedHashMap<>();

		new Properties() {
			@Override
			public Object put(Object key, Object value) {
				return map.put((String) key, (String) value);
			}
		}.load(stream);

		List<VariableResolver> resolvers = SafeServiceLoader.asList(
			framework,
			VariableResolver.class,
			"variable resolvers"
		);

		for (VariableResolver resolver : resolvers) {
			String prefix = resolver.getPrefix();
			String regex = Pattern.quote(prefix) + "\\{([^|{}]+)(?:\\|([^{}]+))?\\}";
			Pattern pattern = Pattern.compile(regex);

			for (Map.Entry<String, String> entry : map.entrySet()) {
				String value = entry.getValue();
				if (value.contains(prefix)) {
					Matcher matcher = pattern.matcher(value);
					StringBuffer buffer = new StringBuffer();
					while (matcher.find()) {
						matcher.appendReplacement(buffer, resolveVariable(matcher, resolver));
					}
					matcher.appendTail(buffer);
					map.put(entry.getKey(), buffer.toString());
				}
			}
		}

		return map;
	}

	/**
	 * Opens an URL, classpath resource, or local file as input stream.
	 *
	 * @param classLoader Class loader to use to open classpath resources
	 * @param file URL, classpath resource, or local file
	 * @return The input stream of the passed file
	 * @throws IOException Failed to open the passed file
	 */
	private static InputStream getInputStream(ClassLoader classLoader, String file) throws IOException {
		if (URL_DETECTION_PATTERN.matcher(file).matches()) {
			return new URL(file).openStream();
		} else {
			InputStream stream = classLoader.getResourceAsStream(file);
			return stream == null ? new FileInputStream(file) : stream;
		}
	}

	/**
	 * Resolves a found variable.
	 *
	 * @param matcher The actual matcher at a found variable placeholder
	 * @param resolver The resolver to resolve the found variable placeholder
	 * @return The replacement text for the found variable placeholder
	 */
	private static String resolveVariable(Matcher matcher, VariableResolver resolver) {
		String name = matcher.group(1).trim();
		String value = resolver.resolve(name);

		if (value == null) {
			value = matcher.group(2);
			if (value == null) {
				InternalLogger.warn(
					null,
					"{}{} \"{}\" could not be found",
					resolver.getName().substring(0, 1).toUpperCase(Locale.ENGLISH),
					resolver.getName().substring(1),
					name
				);
				value = matcher.group();
			} else {
				value = value.trim();
			}
		}

		return value;
	}

}
