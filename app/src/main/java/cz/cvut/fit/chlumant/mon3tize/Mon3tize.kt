package cz.cvut.fit.chlumant.mon3tize

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.cvut.fit.chlumant.mon3tize.components.AdBanner
import kotlinx.coroutines.flow.Flow

object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

//    nemel by byt leak kdyz je to globalni applicationContext ne?
    @Suppress("StaticFieldLeak")
    private lateinit var freemiumManager: FreemiumManager

    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration
        this.freemiumManager = FreemiumManager(context.applicationContext)
    }

    suspend fun enableFreemium() {
        freemiumManager.enableFreemium()
    }

    suspend fun disableFreemium() {
        freemiumManager.disableFreemium()
    }

    val freemiumFlow: Flow<Boolean>
        get() = freemiumManager.isFreemiumEnabled

    suspend fun setFirstLaunch(value: Boolean) {
        freemiumManager.setFirstLaunch(value)
    }

    val isFirstLaunch: Flow<Boolean>
        get() = freemiumManager.isFirstLaunch

    @Composable
    fun LockedContent(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val freemium by freemiumFlow.collectAsState(initial = false)
        if (freemium) {
            content()
        }
    }
}
