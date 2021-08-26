package org.tinylog.impl.policies;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BundlePolicyTest {

	@Mock
	private Path file;

	@Mock(lenient = true)
	private Policy firstPolicy;

	@Mock(lenient = true)
	private Policy secondPolicy;

	/**
	 * Verifies that a log file will be discontinued, if all wrapped policies return {@code false} for
	 * {@link Policy#canContinueFile(Path)}.
	 */
	@Test
	void discontinueFileByBothPolicies() throws Exception {
		when(firstPolicy.canContinueFile(file)).thenReturn(false);
		when(secondPolicy.canContinueFile(file)).thenReturn(false);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		assertThat(bundlePolicy.canContinueFile(file)).isFalse();
	}

	/**
	 * Verifies that a log file will be discontinued, if one of the wrapped policies returns {@code false} for
	 * {@link Policy#canContinueFile(Path)}.
	 */
	@Test
	void discontinueFileByOnePolicy() throws Exception {
		when(firstPolicy.canContinueFile(file)).thenReturn(false);
		when(secondPolicy.canContinueFile(file)).thenReturn(true);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		assertThat(bundlePolicy.canContinueFile(file)).isFalse();
	}

	/**
	 * Verifies that a log file will be continued, if all wrapped policies return {@code true} for
	 * {@link Policy#canContinueFile(Path)}.
	 */
	@Test
	void continueFileByBothPolicies() throws Exception {
		when(firstPolicy.canContinueFile(file)).thenReturn(true);
		when(secondPolicy.canContinueFile(file)).thenReturn(true);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		assertThat(bundlePolicy.canContinueFile(file)).isTrue();
	}

	/**
	 * Verifies that a log file will be discontinued, if all wrapped policies return {@code false} for
	 * {@link Policy#canAcceptLogEntry(int)}.
	 */
	@Test
	void discontinueLogEntryByBothPolicies() throws Exception {
		when(firstPolicy.canAcceptLogEntry(42)).thenReturn(false);
		when(secondPolicy.canAcceptLogEntry(42)).thenReturn(false);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		bundlePolicy.init(file);
		assertThat(bundlePolicy.canAcceptLogEntry(42)).isFalse();
	}

	/**
	 * Verifies that a log file will be discontinued, if one of the wrapped policies returns {@code false} for
	 * {@link Policy#canAcceptLogEntry(int)}.
	 */
	@Test
	void discontinueLogEntryByOnePolicy() throws Exception {
		when(firstPolicy.canAcceptLogEntry(42)).thenReturn(false);
		when(secondPolicy.canAcceptLogEntry(42)).thenReturn(true);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		bundlePolicy.init(file);
		assertThat(bundlePolicy.canAcceptLogEntry(42)).isFalse();
	}

	/**
	 * Verifies that a log file will be discontinued, if all wrapped policies return {@code false} for
	 * {@link Policy#canAcceptLogEntry(int)}.
	 */
	@Test
	void continueLogEntryByBothPolicies() throws Exception {
		when(firstPolicy.canAcceptLogEntry(42)).thenReturn(true);
		when(secondPolicy.canAcceptLogEntry(42)).thenReturn(true);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		bundlePolicy.init(file);
		assertThat(bundlePolicy.canAcceptLogEntry(42)).isTrue();
	}

	/**
	 * Verifies that all exceptions thrown during the policy initialization are rethrown as single exception.
	 */
	@Test
	void failedInitialization() throws Exception {
		Exception firstException = new RuntimeException();
		doThrow(firstException).when(firstPolicy).init(file);

		Exception secondException = new RuntimeException();
		doThrow(secondException).when(secondPolicy).init(file);

		BundlePolicy bundlePolicy = new BundlePolicy(ImmutableList.of(firstPolicy, secondPolicy));
		Throwable thrown = catchThrowable(() -> bundlePolicy.init(file));

		assertThat(thrown)
			.isEqualTo(firstException)
			.hasSuppressedException(secondException);
	}

}
