package org.tinylog.core.runtime;

import org.tinylog.core.internal.InternalLogger;

import android.os.Build;
import android.os.Process;

/**
 * Abstract runtime implementation for Android.
 */
public abstract class AbstractAndroidRuntime extends AbstractRuntime {

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public AbstractAndroidRuntime(InternalLogger logger) {
        super(logger);
    }

    @Override
    public String getVirtualMachine() {
        return "Android Runtime " + Build.VERSION.RELEASE;
    }

    @Override
    public long getProcessId() {
        return Process.myPid();
    }

    @Override
    public String getDefaultWriter() {
        return "logcat";
    }

}
