@file:Suppress("DEPRECATION")

package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger

internal object AuthManager : FirebaseAuthActions {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun signInWithGoogleToken(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    onResult(true, uid)
                } else {
                    Mon3tizeLogger.e("AuthManager", "Přihlášení selhalo: ${task.exception?.message}")
                    onResult(false, null)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        Mon3tizeLogger.d("AuthManager", "Uživatel byl odhlášen.")
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

    override fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}
