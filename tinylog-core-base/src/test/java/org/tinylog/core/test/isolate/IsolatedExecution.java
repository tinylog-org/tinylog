package org.tinylog.core.test.isolate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit extension annotation for isolating classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(IsolateInvocationExtension.class)
public @interface IsolatedExecution {

    /**
     * These classes will be reloaded for each test.
     *
     * @return The classes to reload for each test.
     */
    Class[] classes() default {};

}
