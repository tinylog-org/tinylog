package org.tinylog.impl.backend;

import java.util.Collections;
import java.util.Map;

/**
 * Implementation of {@link InheritableThreadLocal} for {@link Map maps}. The initial value is an empty map instead of
 * {@code null}.
 *
 * @param <K> The type for map entry keys
 * @param <V> The type for map entry values
 */
class InheritableMapThreadLocal<K, V> extends InheritableThreadLocal<Map<K, V>> {

    /** */
    InheritableMapThreadLocal() {
    }

    @Override
    protected Map<K, V> initialValue() {
        return Collections.emptyMap();
    }

}
