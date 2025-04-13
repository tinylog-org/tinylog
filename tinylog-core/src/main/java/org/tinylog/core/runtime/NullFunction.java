package org.tinylog.core.runtime;

import java.util.function.Function;

/**
 * Function that returns always {@code null}.
 */
class NullFunction implements Function<String, Object> {

    /** */
    NullFunction() {
    }

    @Override
    public Object apply(String className) {
        return null;
    }

}
