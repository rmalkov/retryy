package io.github.rmalkov.retryy

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RetryTest {
    @Test
    fun `successful case`() {
        // assume
        var attempts = 0
        // act
        val actual = retry(1.seconds, NullBackoff) {
            ++attempts
            "success"
        }
        // assert
        assertEquals(1, attempts)
        assertTrue(actual.isSuccess)
        assertEquals("success", actual.getOrNull())
    }

    @Test
    fun `should return failure with MaxAttemptsExceededException after 1 + backoffMaxAttempts attempts when task cannot be completed`() {
        // assume
        val backoffMaxAttempts = 3
        val backoff = ConstantBackoff(maxAttempts = backoffMaxAttempts, delay = 0.seconds, jitter = false)
        var attempts = 0
        // act
        val actual = retry(1.seconds, backoff) {
            ++attempts
            throw RuntimeException()
        }
        // assert
        assertEquals(backoffMaxAttempts + 1, attempts)
        assertTrue(actual.isFailure)
        assertIs<MaxAttemptsExceededException>(actual.exceptionOrNull())
    }

    @Test
    fun `should return after first successful attempt`() {
        // assume
        val backoffMaxAttempts = 10
        val backoff = ConstantBackoff(maxAttempts = backoffMaxAttempts, delay = 0.seconds, jitter = false)
        var attempts = 0
        // act
        val actual = retry(1.seconds, backoff) {
            ++attempts
            if (attempts < 5) throw RuntimeException()
            123
        }
        // assert
        assertEquals(5, attempts)
        assertTrue(actual.isSuccess)
        assertEquals(123, actual.getOrNull())
    }

    @Test
    fun `should cancel attempts with timeout`() {
        // assume
        val backoffMaxAttempts = 10
        val backoff = ConstantBackoff(maxAttempts = backoffMaxAttempts, delay = 0.seconds, jitter = false)
        var attempts = 0
        // act
        val actual = retry(100.milliseconds, backoff) {
            ++attempts
            val d = attempts * 20L
            Thread.sleep(200 - d)
            "OK"
        }
        // assert
        assertEquals(6, attempts)
        assertTrue(actual.isSuccess)
        assertEquals("OK", actual.getOrNull())
    }
}
