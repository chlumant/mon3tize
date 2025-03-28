package cz.cvut.fit.chlumant.demoApp

import android.app.Application
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration

class Mon3tizeDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Mon3tize.setUp(
            configuration = Mon3tizeConfiguration(
                enableFreemium = true,
            ),
            context = applicationContext
        )
    }
}
