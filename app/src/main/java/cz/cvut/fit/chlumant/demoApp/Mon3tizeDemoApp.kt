package cz.cvut.fit.chlumant.demoApp

import android.app.Application
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

//        val settings = FirebaseFirestoreSettings.Builder()
//            .setPersistenceEnabled(true)
//            .build()
//
//        FirebaseFirestore.getInstance().firestoreSettings = settings

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



