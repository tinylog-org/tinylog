package org.tinylog.test.junit.isolate;

/**
 * Functional interface for creating a new object instance via reflections.
 */
@FunctionalInterface
public interface ObjectSupplier {

    /**
     * Creates a new object instance.
     *
     * @return The created object instance
     * @throws ReflectiveOperationException Failed to create an object instance
     */
    Object create() throws ReflectiveOperationException;

}
