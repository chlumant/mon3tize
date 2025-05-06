package cz.cvut.fit.chlumant.mon3tize.util

import android.util.Log
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration

object Mon3tizeLogger {

    fun e(tag: String, message: String, exception: Throwable? = null) {
        if (Mon3tize.logLevel == Mon3tizeConfiguration.LogLevel.ERROR || Mon3tize.logLevel == Mon3tizeConfiguration.LogLevel.ALL) {
            Log.e(tag, message, exception)
        }
    }

    fun d(tag: String, message: String, exception: Throwable? = null) {
        if (Mon3tize.logLevel == Mon3tizeConfiguration.LogLevel.ALL) {
            Log.d(tag, message, exception)
        }
    }
}