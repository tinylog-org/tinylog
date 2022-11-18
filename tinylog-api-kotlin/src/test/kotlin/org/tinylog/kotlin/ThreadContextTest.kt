package org.tinylog.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.tinylog.core.Framework
import org.tinylog.core.Tinylog
import org.tinylog.core.backend.LoggingBackend
import org.tinylog.core.context.ContextStorage

internal class ThreadContextTest {
    companion object {
        private lateinit var storage: ContextStorage
        private lateinit var backend: LoggingBackend
        private lateinit var tinylogMock: MockedStatic<Tinylog>

        /**
         * Initializes all mocks.
         */
        @BeforeAll
        @JvmStatic
        fun create() {
            storage = mock()

            backend = mock {
                on { contextStorage } doReturn storage
            }

            tinylogMock = mockStatic(Tinylog::class.java).apply {
                `when`<Any> {
                    Tinylog.getFramework()
                }.thenReturn(object : Framework(false, false) {
                    override fun getLoggingBackend() = backend
                })
            }
        }

        /**
         * Restores the mocked tinylog class.
         */
        @AfterAll
        @JvmStatic
        fun dispose() {
            tinylogMock.close()
        }
    }

    /**
     * Resets the context storage.
     */
    @AfterEach
    fun reset() {
        org.mockito.kotlin.reset(storage)
    }

    /**
     * Verifies that all stored kwy value pairs can be received.
     */
    @Test
    fun receiveMapping() {
        whenever(storage.mapping).thenReturn(mapOf("foo" to "42"))
        assertThat(ThreadContext.mapping).isEqualTo(mapOf("foo" to "42"))
    }

    /**
     * Verifies that a value can be received by its associated key.
     */
    @Test
    fun receiveValue() {
        whenever(storage["foo"]).thenReturn("42")
        assertThat(ThreadContext["foo"]).isEqualTo("42")
    }

    /**
     * Verifies that `null` can be stored as value.
     */
    @Test
    fun putNull() {
        ThreadContext.put("foo", null)
        verify(storage).put("foo", null)
    }

    /**
     * Verifies that an integer will be stored as string.
     */
    @Test
    fun putInteger() {
        ThreadContext.put("foo", 42)
        verify(storage).put("foo", "42")
    }

    /**
     * Verifies that a string value can be stored.
     */
    @Test
    fun putString() {
        ThreadContext.put("foo", "bar")
        verify(storage).put("foo", "bar")
    }

    /**
     * Verifies that a value can be removed by its associated key.
     */
    @Test
    fun removeValue() {
        ThreadContext.remove("foo")
        verify(storage).remove("foo")
    }

    /**
     * Verifies that all stored values can be removed.
     */
    @Test
    fun clearAllValues() {
        ThreadContext.clear()
        verify(storage).clear()
    }

    /**
     * Verifies that the original mapping will be restored after executing code with an independent context.
     */
    @Test
    fun executeIndependentContext() {
        whenever(storage.mapping).thenReturn(mapOf("foo" to "42"))

        ThreadContext.withIndependentContext {
            whenever(storage.mapping).thenThrow(UnsupportedOperationException::class.java)
            verify(storage, never()).clear()
            verify(storage, never()).replace(anyMap())
        }

        verify(storage).replace(mapOf("foo" to "42"))
    }

    /**
     * Verifies that the original mapping will be restored after executing code with an empty context.
     */
    @Test
    fun executionEmptyContext() {
        whenever(storage.mapping).thenReturn(mapOf("foo" to "42"))

        ThreadContext.withEmptyContext {
            whenever(storage.mapping).thenThrow(UnsupportedOperationException::class.java)
            verify(storage).clear()
            verify(storage, never()).replace(anyMap())
        }

        verify(storage).replace(mapOf("foo" to "42"))
    }
}
