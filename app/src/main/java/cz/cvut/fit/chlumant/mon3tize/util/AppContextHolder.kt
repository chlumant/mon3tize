package cz.cvut.fit.chlumant.mon3tize.util

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextHolder {
    private var _context: Context? = null

    val context: Context
        get() = _context
            ?: throw IllegalStateException("AppContextHolder is not initialized. Call AppContextHolder.init(applicationContext) first.")

    fun init(appContext: Context) {
        _context = appContext.applicationContext
    }
}
