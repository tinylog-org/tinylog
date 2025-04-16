package org.tinylog;

import java.util.function.Supplier;

/**
 * Abstract logger class for all concrete loggers.
 */
public abstract class AbstractLogger {

    /**
     * Resolves an array of suppliers by invoking their {@code get()} method.
     *
     * @param suppliers The suppliers to resolve
     * @return The returned values of the passed suppliers
     */
    protected static Object[] resolve(Supplier<?>... suppliers) {
        Object[] values = new Object[suppliers.length];
        for (int i = 0; i < suppliers.length; ++i) {
            values[i] = suppliers[i].get();
        }
        return values;
    }

}
