package cz.cvut.fit.chlumant.demoApp

import android.app.Application
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

class Mon3tizeDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Mon3tize.setUp(
            configuration = Mon3tizeConfiguration(
                freemium = Mon3tizeConfiguration.Freemium.Enabled(10.days),
                adMobEnabled = true,
            ),
            context = applicationContext
        )
        CoroutineScope(Dispatchers.IO).launch {
            Mon3tize.freemiumManager.synchronizeWithFirebase()
        }
    }
}

