package org.tinylog.core.runtime;

import java.util.function.Supplier;

/**
 * Supplier that returns always {@code null}.
 */
class NullSupplier implements Supplier<Object> {

    /** */
    NullSupplier() {
    }

    @Override
    public Object get() {
        return null;
    }

}
