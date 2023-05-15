package io.github.rmalkov.retryy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ExponentialBackoffTest {
    @Test
    fun defaults() {
        val backoff = ExponentialBackoff()
        assertEquals(1.seconds, backoff.next())
        assertEquals(2.seconds, backoff.next())
        assertEquals(4.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun factor() {
        val backoff = ExponentialBackoff(factor = 1.5)
        assertEquals(1.0.seconds, backoff.next())
        assertEquals(1.5.seconds, backoff.next())
        assertEquals(2.25.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun jitter() {
        val backoff = ExponentialBackoff(jitter = true)
        assertTrue(backoff.next().let { it >= 1.seconds && it < 2.seconds })
        assertTrue(backoff.next().let { it >= 2.seconds && it < 3.seconds })
        assertTrue(backoff.next().let { it >= 4.seconds && it < 5.seconds })
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun minDelay() {
        val backoff = ExponentialBackoff(minDelay = 500.milliseconds)
        assertEquals(500.milliseconds, backoff.next())
        assertEquals(1000.milliseconds, backoff.next())
        assertEquals(2000.milliseconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun maxDelay() {
        val backoff = ExponentialBackoff(maxDelay = 2.seconds)
        assertEquals(1.seconds, backoff.next())
        assertEquals(2.seconds, backoff.next())
        assertEquals(2.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }

    @Test
    fun maxTimes() {
        val backoff = ExponentialBackoff(maxAttempts = 1)
        assertEquals(1.seconds, backoff.next())
        assertFailsWith<NoSuchElementException> { backoff.next() }
    }
}
