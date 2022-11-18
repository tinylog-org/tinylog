package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * Static marker binder for using the in-built {@link BasicMarker} class. These markers will be translated to tags for
 * tinylog.
 */
public final class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * Singleton instance of this static marker binder.
     */
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    private final IMarkerFactory factory;

    /** */
    private StaticMarkerBinder() {
        factory = new BasicMarkerFactory();
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return factory;
    }

    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }

}
