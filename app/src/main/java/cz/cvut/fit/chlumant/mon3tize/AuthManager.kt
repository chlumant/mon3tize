package cz.cvut.fit.chlumant.mon3tize

import android.util.Log
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


object AuthManager {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signInWithGoogleToken(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    onResult(true, uid)
                } else {
                    Log.e("AuthManager", "Přihlášení selhalo: ${task.exception?.message}")
                    onResult(false, null)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        Log.d("AuthManager", "Uživatel byl odhlášen.")
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUid(): String? {
        return auth.currentUser?.uid
    }

    fun getEmail(): String? {
        return auth.currentUser?.email
    }

    fun isAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }

    fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}