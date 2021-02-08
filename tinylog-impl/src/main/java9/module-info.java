import org.tinylog.impl.format.ClassNamePlaceholderBuilder;
import org.tinylog.impl.format.ClassPlaceholderBuilder;
import org.tinylog.impl.format.ContextPlaceholderBuilder;
import org.tinylog.impl.format.DatePlaceholderBuilder;
import org.tinylog.impl.format.ExceptionPlaceholderBuilder;
import org.tinylog.impl.format.FilePlaceholderBuilder;
import org.tinylog.impl.format.LevelPlaceholderBuilder;
import org.tinylog.impl.format.LinePlaceholderBuilder;
import org.tinylog.impl.format.MessageOnlyPlaceholderBuilder;
import org.tinylog.impl.format.MessagePlaceholderBuilder;
import org.tinylog.impl.format.MethodPlaceholderBuilder;
import org.tinylog.impl.format.PackagePlaceholderBuilder;
import org.tinylog.impl.format.PlaceholderBuilder;
import org.tinylog.impl.format.ProcessIdPlaceholderBuilder;
import org.tinylog.impl.format.SeverityCodePlaceholderBuilder;
import org.tinylog.impl.format.TagPlaceholderBuilder;
import org.tinylog.impl.format.ThreadIdPlaceholderBuilder;
import org.tinylog.impl.format.ThreadPlaceholderBuilder;
import org.tinylog.impl.format.TimestampPlaceholderBuilder;
import org.tinylog.impl.format.UptimePlaceholderBuilder;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.impl.writer.ConsoleWriterBuilder;

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

	uses WriterBuilder;
	provides WriterBuilder with
		ConsoleWriterBuilder;

	exports org.tinylog.impl;
	exports org.tinylog.impl.format;
	exports org.tinylog.impl.writer;
}
