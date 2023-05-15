package io.github.rmalkov.retryy

object NullBackoff : Backoff {
    override fun hasNext() = false
    override fun next() = throw NoSuchElementException()
}
