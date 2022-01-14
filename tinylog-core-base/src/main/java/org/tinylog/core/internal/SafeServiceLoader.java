package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.tinylog.core.Framework;

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
	 * @param framework The actual logging framework instance
	 * @param service The interface or abstract class representing the service
	 * @param name The human-readable service name for logging
	 * @param <S> The service type
	 * @return All found service implementations in the current classpath
	 */
	public static <S> List<S> asList(Framework framework, Class<S> service, String name) {
		List<S> list = new ArrayList<>();
		load(framework, service, name, list::add);
		return list;
	}

	/**
	 * Retrieves all service implementations and returns them as a mapped list.
	 *
	 * @param framework The actual logging framework instance
	 * @param service The interface or abstract class representing the service
	 * @param name The human-readable service name for logging
	 * @param mapper The mapping function to apply for found service implementations
	 * @param <S> The service type
	 * @param <R> The mapped type
	 * @return Mapped elements for all found service implementations
	 */
	public static <S, R> List<R> asList(Framework framework, Class<S> service, String name, Function<S, R> mapper) {
		List<R> list = new ArrayList<>();
		load(framework, service, name, implementation -> list.add(mapper.apply(implementation)));
		return list;
	}

	/**
	 * Loads all service implementations.
	 *
	 * @param framework The actual logging framework instance
	 * @param service The interface or abstract class representing the service
	 * @param name The human-readable service name for logging
	 * @param action Consumer for found service implementations
	 * @param <S> The service type
	 */
	public static <S> void load(Framework framework, Class<S> service, String name, Consumer<S> action) {
		Iterable<S> iterable = ServiceLoader.load(service, framework.getClassLoader());
		Stream<S> stream = StreamSupport.stream(iterable.spliterator(), false);

		InternalLogger.debug(
			null,
			"Found {}s: {}",
			name,
			stream.map(implementation -> implementation.getClass().getName()).collect(Collectors.toList())
		);

		iterable.forEach(implementation -> execute(implementation, "initialize", action));
	}

	/**
	 * Executes a service implementation. If the execution is successful, the result will be added to the passed target
	 * collection.
	 *
	 * @param target The target collection to add the result to
	 * @param implementation The service implementation to execute
	 * @param activity The human-readable activity as verb for logging
	 * @param mapper The mapping function to apply for the passed service implementations
	 * @param <S> The service type
	 * @param <R> The result type
	 */
	public static <S, R> void execute(Collection<R> target, S implementation, String activity, Function<S, R> mapper) {
		execute(implementation, activity, instance -> target.add(mapper.apply(instance)));
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
