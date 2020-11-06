package org.tinylog.core.format.value;

import java.util.Locale;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberFormatBuilderTest {

	/**
	 * Verifies that the builder can create an instance of {@link NumberFormat}.
	 */
	@Test
	void creation() {
		NumberFormatBuilder builder = new NumberFormatBuilder();
		assertThat(builder.create(Locale.GERMANY)).isInstanceOf(NumberFormat.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(ValueFormatBuilder.class))
			.anyMatch(builder -> builder instanceof NumberFormatBuilder);
	}

}
