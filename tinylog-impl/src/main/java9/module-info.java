import org.tinylog.impl.format.ClassNamePlaceholderBuilder;
import org.tinylog.impl.format.ClassPlaceholderBuilder;
import org.tinylog.impl.format.ContextPlaceholderBuilder;
import org.tinylog.impl.format.DatePlaceholderBuilder;
import org.tinylog.impl.format.ExceptionPlaceholderBuilder;
import org.tinylog.impl.format.FilePlaceholderBuilder;
import org.tinylog.impl.format.LevelPlaceholderBuilder;
import org.tinylog.impl.format.LinePlaceholderBuilder;
import org.tinylog.impl.format.MessageOnlyPlaceholderBuilder;
import org.tinylog.impl.format.PlaceholderBuilder;
import org.tinylog.impl.format.SeverityCodePlaceholderBuilder;
import org.tinylog.impl.writer.WriterBuilder;

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
		SeverityCodePlaceholderBuilder;

	uses WriterBuilder;

	exports org.tinylog.impl;
}
