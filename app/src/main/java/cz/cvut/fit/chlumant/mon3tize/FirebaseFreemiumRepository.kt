package cz.cvut.fit.chlumant.mon3tize

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseFreemiumRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    suspend fun initialize(): String {
        val user = auth.currentUser ?: auth.signInAnonymously().await().user
        return user?.uid ?: throw IllegalStateException("Authentication failed")
    }

    suspend fun saveFreemiumState(enabled: Boolean) {
        val uid = initialize()
        val userDoc = firestore.collection("users").document(uid)
        userDoc.set(mapOf("freemiumEnabled" to enabled)).await()
    }

    suspend fun loadFreemiumState(): Boolean {
        val uid = initialize()
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getBoolean("freemiumEnabled") ?: false
    }
}

//jak budeme resit, ze FreemiumManager i FirebaseFreemiumRepository jsou
// casti modulu mon3tize, coz je knihovna, ktera budem pouzivana napric ruznymi aplikacemi?