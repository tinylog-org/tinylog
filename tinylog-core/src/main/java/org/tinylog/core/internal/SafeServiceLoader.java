package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Service loader wrapper that catches and logs thrown exceptions while executing service implementations.
 *
 * @see ServiceLoader
 */
public final class SafeServiceLoader {

    /** */
    private SafeServiceLoader() {
    }

    /**
     * Retrieves all service implementations as a list.
     *
     * @param loader The class loader to use for loading the service files and service implementation classes
     * @param service The interface or abstract class representing the service
     * @param name The human-readable plural service name for logging
     * @param <S> The service type
     * @return All found service implementations in the current classpath
     */
    public static <S> List<S> asList(ClassLoader loader, Class<S> service, String name) {
        List<S> list = new ArrayList<>();
        load(loader, service, name, list::add);
        return list;
    }

    /**
     * Retrieves all service implementations and returns them as a mapped list.
     *
     * @param loader The class loader to use for loading the service files and service implementation classes
     * @param service The interface or abstract class representing the service
     * @param name The human-readable plural service name for logging
     * @param mapper The mapping function to apply for found service implementations
     * @param <S> The service type
     * @param <R> The mapped type
     * @return Mapped elements for all found service implementations
     */
    public static <S, R> List<R> asList(ClassLoader loader, Class<S> service, String name, Function<S, R> mapper) {
        List<R> list = new ArrayList<>();
        load(loader, service, name, implementation -> list.add(mapper.apply(implementation)));
        return list;
    }

    /**
     * Loads all service implementations.
     *
     * @param loader The class loader to use for loading the service files and service implementation classes
     * @param service The interface or abstract class representing the service
     * @param name The human-readable plural service name for logging
     * @param action Consumer for found service implementations
     * @param <S> The service type
     */
    public static <S> void load(ClassLoader loader, Class<S> service, String name, Consumer<S> action) {
        Iterable<S> iterable = ServiceLoader.load(service, loader);
        Stream<S> stream = StreamSupport.stream(iterable.spliterator(), false);

        InternalLogger.debug(
            null,
            "Found {}: {}",
            name,
            stream.map(implementation -> implementation.getClass().getName()).collect(Collectors.toList())
        );

        iterable.forEach(implementation -> execute(implementation, "initialize", action));
    }

    /**
     * Executes a service implementation.
     *
     * @param implementation The service implementation to execute
     * @param activity The human-readable activity as verb for logging
     * @param action The action to execute for the passed service implementation
     * @param <S> The service type
     */
    public static <S> void execute(S implementation, String activity, Consumer<S> action) {
        try {
            action.accept(implementation);
        } catch (Exception ex) {
            InternalLogger.error(ex, "Failed to {} {}", activity, implementation.getClass().getName());
        }
    }

}
