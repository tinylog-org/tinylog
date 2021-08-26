package org.tinylog.impl.backend;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class LevelConfigurationTest {

	@Inject
	private Log log;

	/**
	 * Verifies parsing an empty level configuration without any passed severity levels.
	 */
	@Test
	void empty() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.emptyList());
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.TRACE);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with a single untagged severity level.
	 */
	@Test
	void generalLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("debug"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with a single custom tagged severity level.
	 */
	@Test
	void taggedLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("foo@error"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("foo", "tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.ERROR);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with a single tagged severity level for tinylog internal log entries.
	 */
	@Test
	void tinylogLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("tinylog@trace"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.OFF);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies parsing a level configuration with a single severity level for the any placeholder {@code '*'}.
	 */
	@Test
	void anyPlaceholderLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("*@debug"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with a single severity level for the plus placeholder {@code '+'}.
	 */
	@Test
	void plusPlaceholderLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("+@debug"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.DEBUG);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with a single severity level for the minus placeholder {@code '-'}.
	 */
	@Test
	void minusPlaceholderLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("-@debug"));
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.OFF);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.OFF);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

	/**
	 * Verifies parsing a level configuration with multiple severity levels.
	 */
	@Test
	void multipleLevels() {
		List<String> levels = ImmutableList.of("debug", "+@info", "foo@trace", "tinylog@error");
		LevelConfiguration configuration = new LevelConfiguration(levels);
		assertThat(configuration.getTags()).containsExactlyInAnyOrder("foo", "tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.INFO);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.TRACE);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.ERROR);
	}

	/**
	 * Verifies parsing a level configuration with an invalid severity level.
	 */
	@Test
	void invalidLevel() {
		LevelConfiguration configuration = new LevelConfiguration(Collections.singletonList("foo"));

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("foo");
		});

		assertThat(configuration.getTags()).containsExactlyInAnyOrder("tinylog");
		assertThat(configuration.getUntaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(configuration.getDefaultTaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(configuration.getTaggedLevel("foo")).isEqualTo(Level.TRACE);
		assertThat(configuration.getTaggedLevel("tinylog")).isEqualTo(Level.WARN);
	}

}
