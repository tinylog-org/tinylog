package org.tinylog.core.runtime;

import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for {@link LegacyAndroidRuntime}.
 */
public class AndroidRuntimeBuilder implements RuntimeBuilder {

    /** */
    public AndroidRuntimeBuilder() {
    }

    @Override
    public boolean isSupported() {
        return System.getProperty("java.runtime.name").equals("Android Runtime");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public RuntimeFlavor create(InternalLogger logger) {
        try {
            Class.forName("java.lang.StackWalker");
            return new ModernAndroidRuntime(logger);
        } catch (ClassNotFoundException ex) {
            return new LegacyAndroidRuntime(logger);
        }
    }

}
