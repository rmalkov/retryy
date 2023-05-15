package io.github.rmalkov.retryy

class MaxAttemptsExceededException : Exception("Task has been attempted too many times or run too long.")
