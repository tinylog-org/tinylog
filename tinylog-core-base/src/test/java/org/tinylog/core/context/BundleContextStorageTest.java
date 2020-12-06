package org.tinylog.core.context;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BundleContextStorageTest {

	private BundleContextStorage bundleStorage;

	@Mock(lenient = true)
	private ContextStorage firstChildStorage;

	@Mock(lenient = true)
	private ContextStorage secondChildStorage;

	/**
	 * Instantiates the bundle context storage with both mock storages as children.
	 */
	@BeforeEach
	void init() {
		bundleStorage = new BundleContextStorage(Arrays.asList(firstChildStorage, secondChildStorage));
	}

	/**
	 * Verifies that the mapping of all child storages are merged correctly. On duplicate key, the context value of
	 * the first storage should win.
	 */
	@Test
	void receiveMapping() {
		when(firstChildStorage.getMapping()).thenReturn(Map.of("foo", "1", "bar", "1"));
		when(secondChildStorage.getMapping()).thenReturn(Map.of("bar", "2", "other", "2"));

		assertThat(bundleStorage.getMapping())
			.containsExactlyInAnyOrderEntriesOf(Map.of("foo", "1", "bar", "1", "other", "2"));
	}

	/**
	 * Verifies that the a value can be received that only exists in the first child storage.
	 */
	@Test
	void receiveValueOfFirstStorage() {
		when(firstChildStorage.get("foo")).thenReturn("1");

		assertThat(bundleStorage.get("foo")).isEqualTo("1");
	}

	/**
	 * Verifies that the a value can be received that only exists in the second child storage.
	 */
	@Test
	void receiveValueOfSecondStorage() {
		when(secondChildStorage.get("foo")).thenReturn("2");

		assertThat(bundleStorage.get("foo")).isEqualTo("2");
	}

	/**
	 * Verifies that the first value is received if a key exists in multiple child storages.
	 */
	@Test
	void receiveDuplicate() {
		when(firstChildStorage.get("foo")).thenReturn("1");
		when(secondChildStorage.get("foo")).thenReturn("2");

		assertThat(bundleStorage.get("foo")).isEqualTo("1");
	}

	/**
	 * Verifies that {@code null} is received if a key does not exist in any storage.
	 */
	@Test
	void receiveMissingValue() {
		assertThat(bundleStorage.get("foo")).isNull();
	}

	/**
	 * Verifies that a new context value is stored in all child storages.
	 */
	@Test
	void putNewValue() {
		bundleStorage.put("foo", "42");

		verify(firstChildStorage).put("foo", "42");
		verify(secondChildStorage).put("foo", "42");
	}

	/**
	 * Verifies that a new replacement mapping is applied to all child storages.
	 */
	@Test
	void replaceWithNewMapping() {
		Map<String, String> mapping = Map.of("foo", "42");

		bundleStorage.replace(mapping);

		verify(firstChildStorage).replace(mapping);
		verify(secondChildStorage).replace(mapping);
	}

	/**
	 * Verifies that the origin mapping of all child storages is restored, if an origin map is passed as replacement
	 * mapping.
	 */
	@Test
	void replaceWithOriginMapping() {
		when(firstChildStorage.getMapping()).thenReturn(Map.of("foo", "1"));
		when(secondChildStorage.getMapping()).thenReturn(Map.of("bar", "2"));

		bundleStorage.replace(bundleStorage.getMapping());

		verify(firstChildStorage).replace(Map.of("foo", "1"));
		verify(secondChildStorage).replace(Map.of("bar", "2"));
	}

	/**
	 * Verifies that the entire origin mapping is applied as replacement mapping to all child storages, if the mapping
	 * was modified.
	 */
	@Test
	void replaceWithModifiedMapping() {
		when(firstChildStorage.getMapping()).thenReturn(Map.of("foo", "1"));
		when(secondChildStorage.getMapping()).thenReturn(Map.of("bar", "2"));

		Map<String, String> mapping = bundleStorage.getMapping();
		mapping.put("foo", "42");
		bundleStorage.replace(mapping);

		verify(firstChildStorage).replace(Map.of("foo", "42", "bar", "2"));
		verify(secondChildStorage).replace(Map.of("foo", "42", "bar", "2"));
	}

	/**
	 * Verifies that the entire origin mapping is applied as replacement mapping to all child storages, if the mapping
	 * is from another bundle context storage instance.
	 */
	@Test
	void replaceWithForeignMapping() {
		when(firstChildStorage.getMapping()).thenReturn(Map.of("foo", "1"));
		when(secondChildStorage.getMapping()).thenReturn(Map.of("bar", "2"));

		BundleContextStorage other = new BundleContextStorage(Arrays.asList(firstChildStorage, secondChildStorage));
		Map<String, String> mapping = other.getMapping();
		bundleStorage.replace(mapping);

		verify(firstChildStorage).replace(Map.of("foo", "1", "bar", "2"));
		verify(secondChildStorage).replace(Map.of("foo", "1", "bar", "2"));
	}

	/**
	 * Verifies that a remove call is delegated to all child storages.
	 */
	@Test
	void removeValue() {
		bundleStorage.remove("foo");

		verify(firstChildStorage).remove("foo");
		verify(secondChildStorage).remove("foo");
	}

	/**
	 * Verifies that a clear call is delegated to all child storages.
	 */
	@Test
	void clearAllValues() {
		bundleStorage.clear();

		verify(firstChildStorage).clear();
		verify(secondChildStorage).clear();
	}

}
