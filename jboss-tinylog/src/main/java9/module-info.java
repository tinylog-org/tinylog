module org.tinylog.api.jboss {
	requires org.tinylog.api;
	requires org.jboss.logging;

	exports org.tinylog.jboss;

	provides org.jboss.logging.LoggerProvider with
			org.tinylog.jboss.TinylogLoggerProvider;
}
