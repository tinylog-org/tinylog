package org.tinylog.core.test;

import java.util.function.Supplier;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * Base extension class for custom JUnit5 extensions.
 */
abstract class AbstractExtension implements BeforeEachCallback, AfterEachCallback {

	private final Namespace namespace;

	/** */
	AbstractExtension() {
		namespace = Namespace.create(getClass());
	}

	/**
	 * Retrieves the stored value for a given value type. If there is no value present, {@code null} will be returned.
	 *
	 * @param context The current extension context
	 * @param type The value type to receive
	 * @param <T> The generic value type
	 * @return The stored value if present, otherwise {@code null}
	 */
	protected <T> T get(ExtensionContext context, Class<T> type) {
		return context.getStore(namespace).get(type, type);
	}

	/**
	 * Retrieves the stored value for a given value type. If there is no value present, a new value will be produced,
	 * stored, and returned.
	 *
	 * @param context The current extension context
	 * @param type The value type to receive
	 * @param producer Supplier function to produce the value, if there is no value present in the store
	 * @param <T> The generic value type
	 * @return The stored value
	 */
	protected <T> T getOrCreate(ExtensionContext context, Class<T> type, Supplier<T> producer) {
		return context.getStore(namespace).getOrComputeIfAbsent(type, key -> producer.get(), type);
	}

	/**
	 * Puts a value to the store. If a value of the same type is already present in the store, it will be overwritten
	 * by the new value.
	 *
	 * @param context The current extension context
	 * @param type The value type to store
	 * @param instance The actual value to store
	 * @param <T> The generic value type
	 */
	protected <T> void put(ExtensionContext context, Class<T> type, T instance) {
		context.getStore(namespace).put(type, instance);
	}

	/**
	 * Removes a value from the store if present.
	 *
	 * @param context The current extension context
	 * @param type The type of the value to remove
	 */
	protected void remove(ExtensionContext context, Class<?> type) {
		if (context.getStore(namespace).remove(type) == null) {
			context.getParent().ifPresent(parent -> remove(parent, type));
		}
	}

}
