package io.github.rmalkov.retryy

import java.util.concurrent.*
import kotlin.time.Duration

data class Retry(
    val timeout: Duration,
    val backoff: Backoff,
    val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
) {
    private fun <T> invoke0(block: () -> T): Result<T> {
        val f = scheduler.submit(Callable {
            runCatching { block() }
        })
        return try {
            f.get(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            f.cancel(true)
            Result.failure(e)
        }
    }

    fun <T> invoke(block: () -> T): Result<T> {
        var result: Result<T>
        var delay: Duration = Duration.ZERO
        do {
            if (delay.isPositive()) {
                var f: ScheduledFuture<*>? = null
                try {
                    val latch = CountDownLatch(1)
                    f = schedule(delay) { latch.countDown() }
                    latch.await()
                } finally {
                    f?.cancel(true)
                }
            }

            result = invoke0(block)
            if (result.isSuccess) break

            delay = try {
                backoff.next()
            } catch (e: NoSuchElementException) {
                result = Result.failure(MaxAttemptsExceededException())
                break
            }
        } while (result.isFailure)

        return result
    }

    private fun schedule(delay: Duration, block: () -> Unit) =
        scheduler.schedule(block, delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}

fun <T> retry(timeout: Duration, backoff: Backoff, block: () -> T) =
    Retry(timeout, backoff).invoke(block)
