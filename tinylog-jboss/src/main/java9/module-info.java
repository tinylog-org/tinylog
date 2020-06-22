module org.tinylog.adapter.jboss {
	requires org.jboss.logging;
	requires org.tinylog.api;

	exports org.tinylog.adapter.jboss;

	provides org.tinylog.provider.LoggingProvider with
			org.tinylog.adapter.jboss.JBossLoggingProvider;
}
