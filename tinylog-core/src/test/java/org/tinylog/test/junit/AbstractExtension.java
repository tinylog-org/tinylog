package org.tinylog.test.junit;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

/**
 * Base extension class for parameterized JUnit extensions.
 */
public abstract class AbstractExtension {

    private final Namespace namespace;

    /** */
    protected AbstractExtension() {
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
        return getStore(context).get(type, type);
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
        return getStore(context).getOrComputeIfAbsent(type, key -> producer.get(), type);
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
        getStore(context).put(type, instance);
    }

    /**
     * Finds all registered annotations.
     *
     * @param context The extension context
     * @param annotationClass The annotation class to search for
     * @param <A> The annotation type
     * @return All found annotations
     */
    protected <A extends Annotation> List<A> findAnnotations(ExtensionContext context, Class<A> annotationClass) {
        List<A> annotations = new ArrayList<>();

        context.getTestInstances()
            .stream()
            .flatMap(instances -> instances.getAllInstances().stream())
            .map(instance -> instance.getClass().getAnnotation(annotationClass))
            .filter(Objects::nonNull)
            .forEachOrdered(annotations::add);

        context.getTestMethod()
            .stream()
            .map(method -> method.getAnnotation(annotationClass))
            .filter(Objects::nonNull)
            .forEachOrdered(annotations::add);

        return annotations;
    }

    /**
     * Gets the store for putting and retrieving values.
     *
     * @param context The current extension context
     * @return The store
     */
    private Store getStore(ExtensionContext context) {
        return context.getStore(namespace);
    }

}
