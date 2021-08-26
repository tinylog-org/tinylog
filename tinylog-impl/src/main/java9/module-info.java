import org.tinylog.impl.format.placeholders.ClassNamePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ClassPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ContextPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.DatePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ExceptionPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.FilePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.LevelPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.LinePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.MessageOnlyPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.MessagePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.MethodPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.PackagePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.PlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ProcessIdPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.SeverityCodePlaceholderBuilder;
import org.tinylog.impl.format.placeholders.TagPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ThreadIdPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.ThreadPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.TimestampPlaceholderBuilder;
import org.tinylog.impl.format.placeholders.UptimePlaceholderBuilder;
import org.tinylog.impl.format.styles.IndentStyleBuilder;
import org.tinylog.impl.format.styles.LengthStyleBuilder;
import org.tinylog.impl.format.styles.MaxLengthStyleBuilder;
import org.tinylog.impl.format.styles.MinLengthStyleBuilder;
import org.tinylog.impl.format.styles.StyleBuilder;
import org.tinylog.impl.policies.PolicyBuilder;
import org.tinylog.impl.policies.SizePolicyBuilder;
import org.tinylog.impl.policies.StartupPolicyBuilder;
import org.tinylog.impl.writers.WriterBuilder;
import org.tinylog.impl.writers.ConsoleWriterBuilder;
import org.tinylog.impl.writers.FileWriterBuilder;
import org.tinylog.impl.writers.LogcatWriterBuilder;

module org.tinylog.impl {
	requires org.tinylog.core;

	uses PlaceholderBuilder;
	provides PlaceholderBuilder with
		ClassNamePlaceholderBuilder,
		ClassPlaceholderBuilder,
		ContextPlaceholderBuilder,
		DatePlaceholderBuilder,
		ExceptionPlaceholderBuilder,
		FilePlaceholderBuilder,
		LevelPlaceholderBuilder,
		LinePlaceholderBuilder,
		MessageOnlyPlaceholderBuilder,
		MessagePlaceholderBuilder,
		MethodPlaceholderBuilder,
		PackagePlaceholderBuilder,
		ProcessIdPlaceholderBuilder,
		SeverityCodePlaceholderBuilder,
		TagPlaceholderBuilder,
		ThreadIdPlaceholderBuilder,
		ThreadPlaceholderBuilder,
		TimestampPlaceholderBuilder,
		UptimePlaceholderBuilder;

	uses StyleBuilder;
	provides StyleBuilder with
		IndentStyleBuilder,
		LengthStyleBuilder,
		MaxLengthStyleBuilder,
		MinLengthStyleBuilder;

	uses PolicyBuilder;
	provides PolicyBuilder with
		SizePolicyBuilder,
		StartupPolicyBuilder;

	uses WriterBuilder;
	provides WriterBuilder with
		ConsoleWriterBuilder,
		FileWriterBuilder,
		LogcatWriterBuilder;

	exports org.tinylog.impl;
	exports org.tinylog.impl.format;
	exports org.tinylog.impl.format.placeholders;
	exports org.tinylog.impl.format.styles;
	exports org.tinylog.impl.policies;
	exports org.tinylog.impl.writers;
}
