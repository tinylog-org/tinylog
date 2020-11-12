package org.tinylog.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.core.loader.ConfigurationLoader;

/**
 * Configuration for tinylog.
 *
 * <p>
 *     The configuration can be set and modified as needed before issuing any log entries. As soon as the first log
 *     entry is issued, the configuration becomes frozen and can no longer be modified.
 * </p>
 */
public class Configuration {

	private static final String FROZEN_MESSAGE =
		"The configuration has already been applied and cannot be modified anymore";

	private static final int MAX_LOCALE_ARGUMENTS = 3;

	private static final String CONFIGURATION_LOADER_PROPERTY = "tinylog.configurationLoader";

	private final Properties properties;
	private boolean frozen;

	/** */
	public Configuration() {
		this.properties = new Properties();
		this.frozen = false;
	}

	/**
	 * Checks if the configuration is already frozen.
	 *
	 * @return {@code true} if the configuration is frozen, {@code false} if still modifiable
	 */
	public boolean isFrozen() {
		synchronized (properties) {
			return frozen;
		}
	}

	/**
	 * Gets the configured locale from property "locale". If the property is not set, {@link Locale#getDefault()} will
	 * be returned instead.
	 *
	 * @return The configured locale or {@link Locale#getDefault()} if not set
	 */
	public Locale getLocale() {
		String value = getValue("locale");
		if (value == null) {
			return Locale.getDefault();
		} else {
			String[] tokens = value.split("_", MAX_LOCALE_ARGUMENTS);
			return new Locale(tokens[0], tokens.length > 1 ? tokens[1] : "", tokens.length > 2 ? tokens[2] : "");
		}
	}

	/**
	 * Searches the value of a specific key.
	 *
	 * @param key Key to search
	 * @return The found value or {@code null} if the key does not exist
	 */
	public String getValue(String key) {
		synchronized (properties) {
			return properties.getProperty(key);
		}
	}

	/**
	 * Searches the values of a specific key.
	 *
	 * <p>
	 *     The found string is split by commas.
	 * </p>
	 *
	 * @param key Key to search
	 * @return The found values or an empty list if the key does not exist
	 */
	public List<String> getList(String key) {
		synchronized (properties) {
			String value = properties.getProperty(key);
			if (value == null) {
				return Collections.emptyList();
			} else {
				List<String> elements = new ArrayList<>();
				for (String element : value.split(",")) {
					String normalized = element.trim();
					if (!normalized.isEmpty()) {
						elements.add(normalized);
					}
				}
				return elements;
			}
		}
	}

	/**
	 * Sets a value for a given key. If another value is already stored under the passed key, the old value will be
	 * overwritten with the new value.
	 *
	 * @param key Key under which the value should to be stored
	 * @param value Value to store
	 * @return The same configuration instance (can be used als fluent API)
	 * @throws UnsupportedOperationException The Configuration has already been applied and cannot be modified anymore
	 */
	public Configuration set(String key, String value) {
		synchronized (properties) {
			if (frozen) {
				throw new UnsupportedOperationException(FROZEN_MESSAGE);
			}

			properties.setProperty(key, value);
		}

		return this;
	}

	/**
	 * Loads the configuration.
	 *
	 * @param framework The actual logging framework instance
	 * @throws UnsupportedOperationException The configuration has already been applied and cannot be modified anymore
	 */
	void load(Framework framework) {
		synchronized (properties) {
			if (frozen) {
				throw new UnsupportedOperationException(FROZEN_MESSAGE);
			} else {
				String name = System.getProperty(CONFIGURATION_LOADER_PROPERTY);

				List<ConfigurationLoader> loaders = SafeServiceLoader.asList(
					framework,
					ConfigurationLoader.class,
					"configuration loader"
				);

				if (name != null) {
					Optional<ConfigurationLoader> optional = loaders
						.stream()
						.filter(loader -> name.toLowerCase(Locale.ENGLISH).equals(loader.getName()))
						.findFirst();

					if (optional.isPresent()) {
						loaders = Collections.singletonList(optional.get());
					} else {
						InternalLogger.error(
							null,
							"Could not find any configuration loader with the name \"{}\" in the classpath",
							name
						);
					}
				}

				loaders.stream()
					.sorted(Comparator.comparingInt(ConfigurationLoader::getPriority).reversed())
					.map(loader -> loader.load(framework))
					.filter(Objects::nonNull)
					.findFirst()
					.ifPresent(properties::putAll);
			}
		}
	}

	/**
	 * Freezes the configuration.
	 *
	 * <p>
	 *     Afterwards, the configuration cannot be modified anymore.
	 * </p>
	 */
	void freeze() {
		synchronized (properties) {
			if (!frozen) {
				frozen = true;
				InternalLogger.debug(null, "Configuration for tinylog: {}", properties);
			}
		}
	}

}
