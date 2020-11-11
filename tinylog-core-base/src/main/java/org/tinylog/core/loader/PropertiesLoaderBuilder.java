package org.tinylog.core.loader;

/**
 * Builder for creating {@link PropertiesLoader PropertiesLoaders}.
 */
public class PropertiesLoaderBuilder implements ConfigurationLoaderBuilder {

	/** */
	public PropertiesLoaderBuilder() {
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
	public PropertiesLoader create() {
		return new PropertiesLoader();
	}

}
