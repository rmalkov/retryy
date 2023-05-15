package io.github.rmalkov.retryy

import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ExponentialBackoff(
    val factor: Double = 2.0,
    val minDelay: Duration = 1.seconds,
    val maxDelay: Duration? = 60.seconds,
    val maxAttempts: Int = 3,
    val jitter: Boolean = false
) : Backoff {
    private var attempts: Int = 0
    private var currentDelay: Duration = minDelay

    override fun hasNext(): Boolean = maxAttempts < 1 || attempts < maxAttempts

    override fun next(): Duration {
        if (!hasNext()) throw NoSuchElementException()

        if (++attempts > 1)
            if (maxDelay != null && currentDelay < maxDelay)
                currentDelay = currentDelay.times(factor)

        return if (!jitter) currentDelay
        else currentDelay.plus(minDelay.times(Random.nextDouble()))
    }
}
