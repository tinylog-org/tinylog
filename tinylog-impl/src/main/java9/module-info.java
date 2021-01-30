import org.tinylog.impl.writer.WriterBuilder;

module org.tinylog.impl {
	requires org.tinylog.core;

	uses WriterBuilder;

	exports org.tinylog.impl;
}
