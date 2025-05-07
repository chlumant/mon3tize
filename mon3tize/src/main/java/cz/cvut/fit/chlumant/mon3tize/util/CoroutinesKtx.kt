package cz.cvut.fit.chlumant.mon3tize.util

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) resume(value)
}

internal fun CancellableContinuation<*>.resumeWithExceptionIfActive(exception: Throwable) {
    if (isActive) resumeWithException(exception)
}
