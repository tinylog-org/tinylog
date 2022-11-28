package org.tinylog.core.runtime;

/**
 * Builder for {@link AndroidRuntime}.
 */
public class AndroidRuntimeBuilder implements RuntimeBuilder {

    /** */
    public AndroidRuntimeBuilder() {
    }

    @Override
    public boolean isSupported() {
        return "Android Runtime".equals(System.getProperty("java.runtime.name"));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public RuntimeFlavor create() {
        return new AndroidRuntime();
    }

}
