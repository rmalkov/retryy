package io.github.rmalkov.retryy

import kotlin.time.Duration

interface Backoff : Iterator<Duration>

fun Iterator<Duration>.asBackoff(): Backoff = let {
    object : Backoff {
        override fun hasNext() = it.hasNext()
        override fun next() = it.next()
    }
}
