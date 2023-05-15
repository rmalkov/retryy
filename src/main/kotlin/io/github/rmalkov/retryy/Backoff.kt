package io.github.rmalkov.retryy

import kotlin.time.Duration

typealias Backoff = Iterator<Duration>
