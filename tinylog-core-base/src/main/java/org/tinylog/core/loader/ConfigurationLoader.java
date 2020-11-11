package org.tinylog.core.loader;

import java.util.Map;

/**
 * Interface for loading configurations.
 */
public interface ConfigurationLoader {

	/**
	 * Tries to load the configuration for tinylog.
	 *
	 * @param classLoader The class loader to use for loading resources from classpath
	 * @return The loaded configuration as map if any configuration could be found and successfully loaded, or
	 *         {@code null} if there is no suitable configuration
	 */
	Map<Object, Object> load(ClassLoader classLoader);

}
