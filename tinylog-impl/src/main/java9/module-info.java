module org.tinylog.impl {
	requires static java.naming;
	requires static java.sql;
	requires org.tinylog.api;

	exports org.tinylog.core;
	exports org.tinylog.path;
	exports org.tinylog.pattern;
	exports org.tinylog.policies;
	exports org.tinylog.throwable;
	exports org.tinylog.writers;
	exports org.tinylog.writers.raw;

	uses org.tinylog.policies.Policy;
	uses org.tinylog.throwable.ThrowableFilter;
	uses org.tinylog.writers.Writer;

	provides org.tinylog.policies.Policy with
			org.tinylog.policies.DailyPolicy,
			org.tinylog.policies.MonthlyPolicy,
			org.tinylog.policies.StartupPolicy,
			org.tinylog.policies.SizePolicy;

	provides org.tinylog.provider.LoggingProvider with
			org.tinylog.core.TinylogLoggingProvider;

	provides org.tinylog.throwable.ThrowableFilter with
			org.tinylog.throwable.DropCauseThrowableFilter,
			org.tinylog.throwable.KeepThrowableFilter,
			org.tinylog.throwable.StripThrowableFilter,
			org.tinylog.throwable.UnpackThrowableFilter;

	provides org.tinylog.writers.Writer with
			org.tinylog.writers.ConsoleWriter,
			org.tinylog.writers.FileWriter,
			org.tinylog.writers.JdbcWriter,
			org.tinylog.writers.LogcatWriter,
			org.tinylog.writers.RollingFileWriter,
			org.tinylog.writers.SharedFileWriter;
}
