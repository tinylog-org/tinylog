package org.tinylog.impl.format.pattern.styles;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.format.pattern.placeholders.BundlePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.LinePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessageOnlyPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractStylePlaceholderTest {

	/**
	 * Verifies that a style placeholder requires the same log entries as the actual wrapped placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		Placeholder actual = new MessagePlaceholder();
		Placeholder styled = new StylePlaceholder(actual);
		assertThat(styled.getRequiredLogEntryValues()).isEqualTo(actual.getRequiredLogEntryValues());
	}

	/**
	 * Verifies that a {@link ValueType#STRING} placeholder is resolved correctly.
	 */
	@Test
	void resolveStringPlaceholder() {
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		assertThat(styled.getType()).isEqualTo(ValueType.STRING);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(styled.getValue(logEntry)).isEqualTo("[Hello World!]");

		logEntry = new LogEntryBuilder().create();
		assertThat(styled.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that a non-string placeholder is resolved as {@link ValueType#STRING}.
	 */
	@Test
	void resolveNumericPlaceholder() {
		Placeholder styled = new StylePlaceholder(new LinePlaceholder());
		assertThat(styled.getType()).isEqualTo(ValueType.STRING);

		LogEntry logEntry = new LogEntryBuilder().lineNumber(42).create();
		assertThat(styled.getValue(logEntry)).isEqualTo("[42]");

		logEntry = new LogEntryBuilder().create();
		assertThat(styled.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that a bundled placeholder is resolved as {@link ValueType#STRING}.
	 */
	@Test
	void resolveBundledPlaceholder() {
		Placeholder prefix = new StaticTextPlaceholder("foo:");
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		Placeholder bundle = new BundlePlaceholder(ImmutableList.of(prefix, styled));
		assertThat(bundle.getType()).isEqualTo(ValueType.STRING);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(bundle.getValue(logEntry)).isEqualTo("foo:[Hello World!]");

		logEntry = new LogEntryBuilder().create();
		assertThat(bundle.getValue(logEntry)).isEqualTo("foo:[]");
	}

	/**
	 * Verifies that a {@link ValueType#STRING} placeholder is rendered correctly.
	 */
	@Test
	void renderStringPlaceholder() {
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("[Hello World!]");

		logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("[]");
	}

	/**
	 * Verifies that a non-string placeholder is rendered correctly.
	 */
	@Test
	void renderNumericPlaceholder() {
		Placeholder styled = new StylePlaceholder(new LinePlaceholder());
		FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().lineNumber(42).create();
		assertThat(renderer.render(logEntry)).isEqualTo("[42]");

		logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("[?]");
	}

	/**
	 * Verifies that a bundled placeholder is rendered correctly.
	 */
	@Test
	void renderBundledPlaceholder() {
		Placeholder prefix = new StaticTextPlaceholder("foo:");
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		Placeholder bundle = new BundlePlaceholder(ImmutableList.of(prefix, styled));
		FormatOutputRenderer renderer = new FormatOutputRenderer(bundle);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo:[Hello World!]");

		logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo:[]");
	}

	/**
	 * Testable non-abstract implementation of {@link AbstractStylePlaceholder}.
	 */
	private static final class StylePlaceholder extends AbstractStylePlaceholder {

		/**
		 * @param placeholder The actual placeholder to style
		 */
		private StylePlaceholder(Placeholder placeholder) {
			super(placeholder);
		}

		@Override
		protected void apply(StringBuilder builder, int start) {
			builder.insert(start, '[');
			builder.append(']');
		}

	}

}