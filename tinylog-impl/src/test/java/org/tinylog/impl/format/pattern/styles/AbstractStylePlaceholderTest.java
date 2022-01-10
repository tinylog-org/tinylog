package org.tinylog.impl.format.pattern.styles;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.placeholders.BundlePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.FilePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.LinePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessageOnlyPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

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
	 * Verifies that a {@link Types#VARCHAR} placeholder is rendered correctly.
	 */
	@Test
	void renderVarCharPlaceholder() {
		Placeholder styled = new StylePlaceholder(new FilePlaceholder());
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().fileName("Foo.java").create();
		assertThat(renderer.render(logEntry)).isEqualTo("[Foo.java]");

		logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("[<file unknown>]");
	}

	/**
	 * Verifies that a {@link Types#LONGVARCHAR} placeholder is rendered correctly.
	 */
	@Test
	void renderLongVarCharPlaceholder() {
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

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
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

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
		PlaceholderRenderer renderer = new PlaceholderRenderer(new BundlePlaceholder(ImmutableList.of(prefix, styled)));

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo:[Hello World!]");

		logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo:[]");
	}

	/**
	 * Verifies that a {@link Types#VARCHAR} placeholder is resolved correctly.
	 */
	@Test
	void resolveVarCharPlaceholder() {
		Placeholder styled = new StylePlaceholder(new FilePlaceholder());

		LogEntry logEntry = new LogEntryBuilder().fileName("Foo.java").create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "[Foo.java]"));

		logEntry = new LogEntryBuilder().create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

	/**
	 * Verifies that a {@link Types#LONGVARCHAR} placeholder is resolved correctly.
	 */
	@Test
	void resolveLongCharPlaceholder() {
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, "[Hello World!]"));

		logEntry = new LogEntryBuilder().create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, null));
	}

	/**
	 * Verifies that a non-string placeholder is resolved as {@link Types#VARCHAR}.
	 */
	@Test
	void resolveNumericPlaceholder() {
		Placeholder styled = new StylePlaceholder(new LinePlaceholder());

		LogEntry logEntry = new LogEntryBuilder().lineNumber(42).create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "[42]"));

		logEntry = new LogEntryBuilder().create();
		assertThat(styled.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

	/**
	 * Verifies that a bundled placeholder is resolved as {@link Types#LONGVARCHAR}.
	 */
	@Test
	void resolveBundledPlaceholder() {
		Placeholder prefix = new StaticTextPlaceholder("foo:");
		Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder());
		Placeholder bundle = new BundlePlaceholder(ImmutableList.of(prefix, styled));

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(bundle.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, "foo:[Hello World!]"));

		logEntry = new LogEntryBuilder().create();
		assertThat(bundle.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, "foo:"));
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
