package org.tinylog.slf4j.backend;

import java.util.Map;

import org.slf4j.MDC;
import org.tinylog.core.context.ContextStorage;

/**
 * SLF4J context storage adapter based on {@link MDC} .
 */
public class Slf4jContextStorage implements ContextStorage {

    /** */
    public Slf4jContextStorage() {
    }

    @Override
    public Map<String, String> getMapping() {
        return MDC.getCopyOfContextMap();
    }

    @Override
    public String get(String key) {
        return MDC.get(key);
    }

    @Override
    public void put(String key, String value) {
        MDC.put(key, value);
    }

    @Override
    public void replace(Map<String, String> mapping) {
        MDC.setContextMap(mapping);
    }

    @Override
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

}
