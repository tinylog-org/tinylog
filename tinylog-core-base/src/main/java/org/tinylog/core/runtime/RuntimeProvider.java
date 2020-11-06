package org.tinylog.core.runtime;

/**
 * Provider for acquiring the appropriate {@link RuntimeFlavor} for the actual virtual machine.
 */
public class RuntimeProvider {

	/** */
	public RuntimeProvider() {
	}

	/**
	 * Provides the appropriate {@link RuntimeFlavor} for the actual virtual machine.
	 *
	 * @return The appropriate runtime instance
	 */
	public RuntimeFlavor getRuntime() {
		if ("Android Runtime".equals(System.getProperty("java.runtime.name"))) {
			return new AndroidRuntime();
		} else {
			return new JavaRuntime();
		}
	}

}
