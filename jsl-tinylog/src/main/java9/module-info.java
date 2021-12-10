module org.tinylog.api.jsl {
	requires org.tinylog.api;

	provides java.lang.System.LoggerFinder with
			org.tinylog.jsl.TinylogLoggerFinder;

	exports org.tinylog.jsl;
}
