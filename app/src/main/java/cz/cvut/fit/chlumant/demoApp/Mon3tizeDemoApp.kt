package cz.cvut.fit.chlumant.demoApp

import android.app.Application
import android.util.Log
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Mon3tizeDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Mon3tize.setUp(
            configuration = Mon3tizeConfiguration(
                enableFreemium = true,
                adMobEnabled = true
            ),
            context = applicationContext
        )
//        tohle asi presunout nekam jinam ne?
        CoroutineScope(Dispatchers.IO).launch {
            Mon3tize.freemiumManager.synchronizeWithFirebase()
        }
    }
}

//asi muzu safely ignorovat - vytahl jsem z firestore a nahradil true (just in case)
//request.time < timestamp.date(2025, 5, 19)

