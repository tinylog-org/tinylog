package org.tinylog.impl.path.segments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class DateTimeSegmentTest {

	@TempDir
	private Path folder;

	@Inject
	private Log log;

	/**
	 * Verifies that null is returned if the folder does not exist.
	 */
	@Test
	void findLatestOfNoneExisting() throws IOException {
		Path nonExistingFolder = folder.resolve("non-existing");

		String latest = new DateTimeSegment("dd-MM-yyyy", Locale.ENGLISH).findLatest(nonExistingFolder, "foo_");
		assertThat(latest).isNull();
	}

	/**
	 * Verifies that null is returned if there are no matching files.
	 */
	@Test
	void findLatestOfNone() throws IOException {
		Files.createFile(folder.resolve("foo_BAR.log"));
		Files.createFile(folder.resolve("bar_01-01-2000.log"));

		String latest = new DateTimeSegment("dd-MM-yyyy", Locale.ENGLISH).findLatest(folder, "foo_");
		assertThat(latest).isNull();
	}

	/**
	 * Verifies that the date of the only matching file is used.
	 *
	 * @param pattern The date-time pattern to test
	 * @param sample The parsable sample to test
	 */
	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(DateTimePatternProvider.class)
	void findLatestOfOne(String pattern, String sample) throws IOException {
		Files.createFile(folder.resolve("foo_" + sample + ".log"));

		String latest = new DateTimeSegment(pattern, Locale.ENGLISH).findLatest(folder, "foo_");
		assertThat(latest).isEqualTo(sample);
	}

	/**
	 * Verifies that the youngest date-time value of multiple matching files is used.
	 *
	 * @param pattern The date-time pattern to test
	 * @param first The first parsable sample to test
	 * @param second The second parsable sample to test
	 * @param match The third and matched parsable sample to test
	 */
	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(DateTimePatternProvider.class)
	void findLatestOfMultiple(String pattern, String first, String second, String match) throws IOException {
		Files.createFile(folder.resolve("foo_" + first + ".log"));
		Files.createFile(folder.resolve("foo_" + second + ".log"));
		Files.createFile(folder.resolve("foo_" + match + ".log"));

		String latest = new DateTimeSegment(pattern, Locale.ENGLISH).findLatest(folder, "foo_");
		assertThat(latest).isEqualTo(match);
	}

	/**
	 * Verifies that none is chosen if there are multiple files with incomparable values.
	 */
	@Test
	void findLatestOfIncomparable() throws IOException {
		Files.createFile(folder.resolve("foo_Z.log"));
		Files.createFile(folder.resolve("foo_+01.log"));

		String latest = new DateTimeSegment("X", Locale.ENGLISH).findLatest(folder, "foo_");
		assertThat(latest).isNull();
	}

	/**
	 * Verifies that the date-time segment appends the passed date-time to the passed string builder by using the
	 * provided date-time formatter.
	 */
	@Test
	void resolveValidPattern() {
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:00:00Z"), ZoneOffset.UTC);
		StringBuilder builder = new StringBuilder("bar/");
		new DateTimeSegment("yyyy-MM-dd_HH-mm", Locale.ENGLISH).resolve(builder, date);

		assertThat(builder).asString().isEqualTo("bar/2000-01-01_12-00");
	}

	/**
	 * Verifies that a date-time segment containing POSIX file separators is appended but a warning is logged.
	 */
	@Test
	void resolvePatternWithPosixSeparators() {
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:00:00Z"), ZoneOffset.UTC);
		StringBuilder builder = new StringBuilder("bar/");
		new DateTimeSegment("MM/dd/yyyy", Locale.ENGLISH).resolve(builder, date);

		assertThat(builder).asString().isEqualTo("bar/01/01/2000");
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.WARN);
			assertThat(entry.getMessage()).contains("file separator", "MM/dd/yyyy");
		});
	}

	/**
	 * Verifies that a date-time segment containing Windows file separators is appended but a warning is logged.
	 */
	@Test
	void resolvePatternWithWindowsSeparators() {
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:00:00Z"), ZoneOffset.UTC);
		StringBuilder builder = new StringBuilder("bar/");
		new DateTimeSegment("MM\\dd\\yyyy", Locale.ENGLISH).resolve(builder, date);

		assertThat(builder).asString().isEqualTo("bar/01\\01\\2000");
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.WARN);
			assertThat(entry.getMessage()).contains("file separator", "MM\\dd\\yyyy");
		});
	}

	/**
	 * Provider for all date-time patterns with samples to test.
	 *
	 * <p>
	 *     All samples are ordered. The oldest value is the first and the youngest the last sample.
	 * </p>
	 */
	private static final class DateTimePatternProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
				Arguments.of("yyyy-MM-dd_HH-mmVV", "2000-01-01_00-00Z", "2000-02-01_00-00Z", "2000-03-01_00-00Z"),
				Arguments.of("yyyy-MM-dd_HH-mmX", "2000-01-01_00-00+01", "2000-02-01_00-00+01", "2000-03-01_00-00+01"),
				Arguments.of("HH-mm_dd-MM-yyyy", "12-30_01-01-2000", "12-30_31-01-2000", "12-30_01-02-2000"),
				Arguments.of("dd-MM-yyyy", "01-01-2000", "31-01-2000", "01-02-2000"),
				Arguments.of("HH-mmX", "00-30-0100", "10-30-0100", "20-30-0100"),
				Arguments.of("HH-mm", "00-30", "10-30", "20-30"),
				Arguments.of("yyyy-MM", "2000-01", "2000-02", "2000-03"),
				Arguments.of("yyyy", "2000", "2001", "2002"),
				Arguments.of("MM-dd", "01-01", "02-01", "03-01"),
				Arguments.of("MMMM", "January", "February", "March"),
				Arguments.of("w", "1", "2", "3"),
				Arguments.of("EEEE", "Monday", "Tuesday", "Wednesday")
			);
		}

	}

}
