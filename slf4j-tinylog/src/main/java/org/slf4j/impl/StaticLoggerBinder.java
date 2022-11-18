package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import org.tinylog.core.Tinylog;
import org.tinylog.slf4j.TinylogLoggerFactory;

/**
 * Static logger factory binder for using tinylog with legacy SLF4J 1.7.
 */
public final class StaticLoggerBinder implements LoggerFactoryBinder {

    /**
     * Request SLF4J 1.7.
     *
     * <p>
     *     This field must not be final to avoid constant folding by the compiler.
     * </p>
     */
    // START IGNORE RULES: StaticVariableName|VisibilityModifier
    public static String REQUESTED_API_VERSION = "1.7";
    // END IGNORE RULES: StaticVariableName|VisibilityModifier

    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    private final ILoggerFactory factory;

    /** */
    private StaticLoggerBinder() {
        factory = new TinylogLoggerFactory(Tinylog.getFramework());
    }

    /**
     * Gets the singleton instance of this static logger binder.
     *
     * @return The static logger binder instance
     */
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return factory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return TinylogLoggerFactory.class.getName();
    }

}
