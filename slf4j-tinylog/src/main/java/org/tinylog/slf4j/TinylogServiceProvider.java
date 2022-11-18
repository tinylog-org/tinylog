package org.tinylog.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;
import org.tinylog.core.Tinylog;

/**
 * Provider for tinylog's implementation for SLF4J 2.
 */
public class TinylogServiceProvider implements SLF4JServiceProvider {

    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    /** */
    public TinylogServiceProvider() {
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0";
    }

    @Override
    public void initialize() {
        loggerFactory = new TinylogLoggerFactory(Tinylog.getFramework());
        markerFactory = new BasicMarkerFactory();
        mdcAdapter = new TinylogMdcAdapter(Tinylog.getFramework().getLoggingBackend().getContextStorage());
    }

}
