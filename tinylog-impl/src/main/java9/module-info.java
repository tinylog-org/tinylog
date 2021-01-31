import org.tinylog.impl.format.ClassPlaceholderBuilder;
import org.tinylog.impl.format.PlaceholderBuilder;
import org.tinylog.impl.writer.WriterBuilder;

module org.tinylog.impl {
	requires org.tinylog.core;

	uses PlaceholderBuilder;
	provides PlaceholderBuilder with
		ClassPlaceholderBuilder;

	uses WriterBuilder;

	exports org.tinylog.impl;
}
