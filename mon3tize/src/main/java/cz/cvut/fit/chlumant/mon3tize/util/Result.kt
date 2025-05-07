package cz.cvut.fit.chlumant.mon3tize.util

public sealed class Result<out T> {

    public data class Success<T>(val data: T) : Result<T>()

    public data class Error(val exception: Throwable) : Result<Nothing>()
}
