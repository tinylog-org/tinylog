package org.tinylog.slf4j;

import java.util.Deque;
import java.util.Map;

import org.slf4j.helpers.ThreadLocalMapOfStacks;
import org.slf4j.spi.MDCAdapter;
import org.tinylog.core.context.ContextStorage;

/**
 * MDC adapter based on tinylog's {@link ContextStorage}.
 */
public class TinylogMdcAdapter implements MDCAdapter {

	private final ContextStorage storage;
	private final ThreadLocalMapOfStacks deques;

	/**
	 * @param storage The context storage to use
	 */
	public TinylogMdcAdapter(ContextStorage storage) {
		this.storage = storage;
		this.deques = new ThreadLocalMapOfStacks();
	}

	@Override
	public void put(String key, String val) {
		storage.put(key, val);
	}

	@Override
	public String get(String key) {
		return storage.get(key);
	}

	@Override
	public void remove(String key) {
		storage.remove(key);
	}

	@Override
	public void clear() {
		storage.clear();
	}

	@Override
	public Map<String, String> getCopyOfContextMap() {
		return storage.getMapping();
	}

	@Override
	public void setContextMap(Map<String, String> contextMap) {
		storage.replace(contextMap);
	}

	@Override
	public void pushByKey(String key, String value) {
		deques.pushByKey(key, value);
	}

	@Override
	public String popByKey(String key) {
		return deques.popByKey(key);
	}

	@Override
	public Deque<String> getCopyOfDequeByKey(String key) {
		return deques.getCopyOfDequeByKey(key);
	}

	@Override
	public void clearDequeByKey(String key) {
		deques.clearDequeByKey(key);
	}

}
