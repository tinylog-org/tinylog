import org.tinylog.impl.format.placeholder.ClassNamePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ClassPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ContextPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.DatePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ExceptionPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.FilePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.LevelPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.LinePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.MessagePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.MethodPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.PackagePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.PlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ProcessIdPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.SeverityCodePlaceholderBuilder;
import org.tinylog.impl.format.placeholder.TagPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ThreadIdPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.ThreadPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.TimestampPlaceholderBuilder;
import org.tinylog.impl.format.placeholder.UptimePlaceholderBuilder;
import org.tinylog.impl.format.style.IndentStyleBuilder;
import org.tinylog.impl.format.style.LengthStyleBuilder;
import org.tinylog.impl.format.style.MaxLengthStyleBuilder;
import org.tinylog.impl.format.style.MinLengthStyleBuilder;
import org.tinylog.impl.format.style.StyleBuilder;
import org.tinylog.impl.policy.PolicyBuilder;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.impl.writer.ConsoleWriterBuilder;
import org.tinylog.impl.writer.FileWriterBuilder;
import org.tinylog.impl.writer.LogcatWriterBuilder;

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

	uses WriterBuilder;
	provides WriterBuilder with
		ConsoleWriterBuilder,
		FileWriterBuilder,
		LogcatWriterBuilder;

	exports org.tinylog.impl;
	exports org.tinylog.impl.format;
	exports org.tinylog.impl.format.placeholder;
	exports org.tinylog.impl.format.style;
	exports org.tinylog.impl.policy;
	exports org.tinylog.impl.writer;
}
