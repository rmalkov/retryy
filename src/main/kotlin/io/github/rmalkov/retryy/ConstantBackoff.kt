package io.github.rmalkov.retryy

import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ConstantBackoff(
    val delay: Duration = 1.seconds,
    val maxAttempts: Int = 3,
    val jitter: Boolean = false
) : Backoff {
    private var attempts: Int = 0
    override fun hasNext() = maxAttempts < 0 || attempts < maxAttempts
    override fun next(): Duration {
        if (!hasNext()) throw NoSuchElementException()
        ++attempts
        return delay.run {
            if (!jitter) this
            else plus(times(Random.nextDouble()))
        }
    }
}
