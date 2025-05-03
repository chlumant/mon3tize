package cz.cvut.fit.chlumant.mon3tize.util

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) resume(value)
}

fun CancellableContinuation<*>.resumeWithExceptionIfActive(exception: Throwable) {
    if (isActive) resumeWithException(exception)
}
