package org.tinylog.core.runtime;

import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for {@link JavaRuntime}.
 */
public class JavaRuntimeBuilder implements RuntimeBuilder {

    /** */
    public JavaRuntimeBuilder() {
    }

    @Override
    public boolean isSupported() {
        return !System.getProperty("java.runtime.name").equals("Android Runtime");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public RuntimeFlavor create(InternalLogger logger) {
        return new JavaRuntime(logger);
    }

}
