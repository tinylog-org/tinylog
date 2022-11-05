package org.tinylog.impl.backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationInfoTest {

	/**
	 * Tests for {@link LocationInfo#resolveClassName(Object)}.
	 */
	@Nested
	class ResolvingClassName {

		/**
		 * Verifies that the fully-qualified class name can be resolved from a {@link StackTraceElement}.
		 */
		@Test
		void resolveFromStackTraceElement() {
			StackTraceElement location = new StackTraceElement(
				"com.example.Foo",
				"bar",
				null,
				-1
			);

			String className = LocationInfo.resolveClassName(location);
			assertThat(className).isEqualTo("com.example.Foo");
		}

		/**
		 * Verifies that the fully-qualified class name can be resolved from a {@link Class}.
		 */
		@Test
		void resolveFromClass() {
			Class<?> location = LocationInfoTest.class;
			String className = LocationInfo.resolveClassName(location);
			assertThat(className).isEqualTo("org.tinylog.impl.backend.LocationInfoTest");
		}

		/**
		 * Verifies that the fully-qualified class name can be resolved from a {@link String}.
		 */
		@Test
		void resolveFromString() {
			String className = LocationInfo.resolveClassName("com.example.Foo");
			assertThat(className).isEqualTo("com.example.Foo");
		}

		/**
		 * Verifies that an empty string will be returned for {@code null}.
		 */
		@Test
		void resolveFromNull() {
			String className = LocationInfo.resolveClassName(null);
			assertThat(className).isEmpty();
		}

	}

	/**
	 * Tests for {@link LocationInfo#resolveStackTraceElement(Object)}.
	 */
	@Nested
	class ResolvingStackTraceElement {

		/**
		 * Verifies that a {@link StackTraceElement} will be just passed through.
		 */
		@Test
		void resolveFromStackTraceElement() {
			StackTraceElement input = new StackTraceElement(
				"com.example.Foo",
				"bar",
				null,
				-1
			);

			StackTraceElement output = LocationInfo.resolveStackTraceElement(input);
			assertThat(output).isSameAs(input);
		}

		/**
		 * Verifies that a {@link Class} can be transformed into a {@link StackTraceElement}.
		 */
		@Test
		void resolveFromClass() {
			Class<?> input = LocationInfoTest.class;
			StackTraceElement output = LocationInfo.resolveStackTraceElement(input);

			assertThat(output)
				.extracting(StackTraceElement::getClassName)
				.isEqualTo("org.tinylog.impl.backend.LocationInfoTest");

			assertThat(output)
				.extracting(StackTraceElement::getMethodName)
				.isEqualTo("<unknown>");

			assertThat(output)
				.extracting(StackTraceElement::getFileName)
				.isNull();

			assertThat(output)
				.extracting(StackTraceElement::getLineNumber)
				.isEqualTo(-1);
		}

		/**
		 * Verifies that a {@link String} with a fully-qualified class name can be transformed into a
		 * {@link StackTraceElement}.
		 */
		@Test
		void resolveFromString() {
			StackTraceElement output = LocationInfo.resolveStackTraceElement("com.example.Foo");

			assertThat(output)
				.extracting(StackTraceElement::getClassName)
				.isEqualTo("com.example.Foo");

			assertThat(output)
				.extracting(StackTraceElement::getMethodName)
				.isEqualTo("<unknown>");

			assertThat(output)
				.extracting(StackTraceElement::getFileName)
				.isNull();

			assertThat(output)
				.extracting(StackTraceElement::getLineNumber)
				.isEqualTo(-1);
		}

		/**
		 * Verifies that {@code null} can be transformed into a default {@link StackTraceElement}.
		 */
		@Test
		void resolveFromNull() {
			StackTraceElement output = LocationInfo.resolveStackTraceElement(null);

			assertThat(output)
				.extracting(StackTraceElement::getClassName)
				.isEqualTo("<unknown>");

			assertThat(output)
				.extracting(StackTraceElement::getMethodName)
				.isEqualTo("<unknown>");

			assertThat(output)
				.extracting(StackTraceElement::getFileName)
				.isNull();

			assertThat(output)
				.extracting(StackTraceElement::getLineNumber)
				.isEqualTo(-1);
		}

	}

}
