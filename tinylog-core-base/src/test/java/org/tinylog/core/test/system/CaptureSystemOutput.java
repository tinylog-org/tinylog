package org.tinylog.core.test.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit extension annotation for capturing any output to {@link System#out} and {@link System#err}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(SystemStreamCaptureExtension.class)
public @interface CaptureSystemOutput {

	/**
	 * Regular expressions for lines that should be excluded from output.
	 *
	 * @return An output line will be discarded silently, if it matches with at least one of the defined regular
	 *         expressions.
	 */
	String[] excludes() default {};

}
