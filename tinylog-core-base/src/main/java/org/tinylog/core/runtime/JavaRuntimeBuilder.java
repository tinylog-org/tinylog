package org.tinylog.core.runtime;

/**
 * Builder for {@link JavaRuntime}.
 */
public class JavaRuntimeBuilder implements RuntimeBuilder {

	/** */
	public JavaRuntimeBuilder() {
	}

	@Override
	public boolean isSupported() {
		return !"Android Runtime".equals(System.getProperty("java.runtime.name"));
	}

	@Override
	public RuntimeFlavor create() {
		return new JavaRuntime();
	}

}
