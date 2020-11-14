package org.tinylog.core.test.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit extension annotation for registering service implementations for {@link ServiceLoader}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(ServiceRegistrationExtension.class)
public @interface RegisterService {

	/**
	 * The service interface.
	 *
	 * @return The service interface class
	 */
	Class<?> service();

	/**
	 * Service implementations to register.
	 *
	 * @return The implementation classes
	 */
	Class<?>[] implementations();

}
