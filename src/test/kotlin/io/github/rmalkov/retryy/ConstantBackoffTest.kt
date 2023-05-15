package io.github.rmalkov.retryy

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class ConstantBackoffTest {
    @Test
    fun defaults() {
        val backoff = ConstantBackoff()
        assertEquals(1.seconds, backoff.next())
        assertEquals(1.seconds, backoff.next())
        assertEquals(1.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun jitter() {
        val backoff = ConstantBackoff(jitter = true)
        assertTrue(backoff.next() > 1.seconds)
    }

    @Test
    fun maxTimes() {
        val backoff = ConstantBackoff(maxAttempts = 1)
        assertEquals(1.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }
}
