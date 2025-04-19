package cz.cvut.fit.chlumant.demoApp

import android.app.Application
import com.google.firebase.FirebaseApp
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.Mon3tizeConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class Mon3tizeDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings
        FirebaseApp.initializeApp(applicationContext)

        Mon3tize.setUp(
            configuration = Mon3tizeConfiguration(
                enableFreemium = true,
            ),
            context = applicationContext
        )
//        CoroutineScope(Dispatchers.IO).launch {
//            Mon3tize.freemiumManager.synchronizeWithFirebase()
//        }
    }
}

//asi muzu safely ignorovat - vytahl jsem z firestore a nahradil true (just in case)
//request.time < timestamp.date(2025, 5, 19)

