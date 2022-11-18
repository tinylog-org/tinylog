package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;
import org.tinylog.core.Tinylog;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.slf4j.TinylogMdcAdapter;

/**
 * Static MDC binder for using tinylog's {@link ContextStorage} with SLF4J 1.7.
 */
// START IGNORE RULES: AbbreviationAsWordInName
public final class StaticMDCBinder {

    /**
     * Singleton instance of this static MDC binder.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private final MDCAdapter adapter;

    /** */
    private StaticMDCBinder() {
        ContextStorage contextStorage = Tinylog.getFramework().getLoggingBackend().getContextStorage();
        this.adapter = new TinylogMdcAdapter(contextStorage);
    }

    /**
     * Gets the MDC adapter implementation.
     * 
     * @return The MDC adapter instance
     */
    public MDCAdapter getMDCA() {
        return adapter;
    }

    /**
     * Gets the fully-qualified MDC adapter class name.
     * 
     * @return The fully-qualified class name of the MDC adapter
     */
    public String getMDCAdapterClassStr() {
        return TinylogMdcAdapter.class.getName();
    }

}
