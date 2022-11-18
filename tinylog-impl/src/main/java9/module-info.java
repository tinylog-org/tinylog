import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.impl.backend.NativeLoggingBackendBuilder;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.format.json.NewlineDelimitedJsonBuilder;
import org.tinylog.impl.format.pattern.FormatPatternBuilder;
import org.tinylog.impl.format.pattern.placeholders.ClassNamePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ClassPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ContextPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.DatePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ExceptionPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.FilePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.LevelPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.LinePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.MessageOnlyPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.MethodPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.PackagePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.PlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ProcessIdPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.SeverityCodePlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.TagPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ThreadIdPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.ThreadPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.TimestampPlaceholderBuilder;
import org.tinylog.impl.format.pattern.placeholders.UptimePlaceholderBuilder;
import org.tinylog.impl.format.pattern.styles.IndentStyleBuilder;
import org.tinylog.impl.format.pattern.styles.LengthStyleBuilder;
import org.tinylog.impl.format.pattern.styles.MaxLengthStyleBuilder;
import org.tinylog.impl.format.pattern.styles.MinLengthStyleBuilder;
import org.tinylog.impl.format.pattern.styles.StyleBuilder;
import org.tinylog.impl.path.segments.CountSegmentBuilder;
import org.tinylog.impl.path.segments.DateTimeSegmentBuilder;
import org.tinylog.impl.path.segments.PathSegmentBuilder;
import org.tinylog.impl.path.segments.ProcessIdSegmentBuilder;
import org.tinylog.impl.policies.DailyPolicyBuilder;
import org.tinylog.impl.policies.MonthlyPolicyBuilder;
import org.tinylog.impl.policies.PolicyBuilder;
import org.tinylog.impl.policies.SizePolicyBuilder;
import org.tinylog.impl.policies.StartupPolicyBuilder;
import org.tinylog.impl.policies.WeeklyPolicyBuilder;
import org.tinylog.impl.writers.WriterBuilder;
import org.tinylog.impl.writers.console.ConsoleWriterBuilder;
import org.tinylog.impl.writers.file.FileWriterBuilder;
import org.tinylog.impl.writers.jdbc.JdbcWriterBuilder;
import org.tinylog.impl.writers.logcat.LogcatWriterBuilder;

module org.tinylog.impl {

    requires java.sql;
    requires org.tinylog.core;

    provides LoggingBackendBuilder with
        NativeLoggingBackendBuilder;

    provides OutputFormatBuilder with
        FormatPatternBuilder,
        NewlineDelimitedJsonBuilder;

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
        DailyPolicyBuilder,
        MonthlyPolicyBuilder,
        SizePolicyBuilder,
        StartupPolicyBuilder,
        WeeklyPolicyBuilder;

    uses PathSegmentBuilder;
    provides PathSegmentBuilder with
        CountSegmentBuilder,
        DateTimeSegmentBuilder,
        ProcessIdSegmentBuilder;

    uses WriterBuilder;
    provides WriterBuilder with
        ConsoleWriterBuilder,
        FileWriterBuilder,
        JdbcWriterBuilder,
        LogcatWriterBuilder;

    exports org.tinylog.impl;
    exports org.tinylog.impl.backend;
    exports org.tinylog.impl.context;
    exports org.tinylog.impl.format;
    exports org.tinylog.impl.format.json;
    exports org.tinylog.impl.format.pattern;
    exports org.tinylog.impl.format.pattern.placeholders;
    exports org.tinylog.impl.format.pattern.styles;
    exports org.tinylog.impl.path;
    exports org.tinylog.impl.path.segments;
    exports org.tinylog.impl.policies;
    exports org.tinylog.impl.writers;
    exports org.tinylog.impl.writers.console;
    exports org.tinylog.impl.writers.file;
    exports org.tinylog.impl.writers.logcat;

}
