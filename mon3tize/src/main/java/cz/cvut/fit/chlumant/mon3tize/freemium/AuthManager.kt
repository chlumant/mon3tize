package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import cz.cvut.fit.chlumant.mon3tize.Mon3tize
import cz.cvut.fit.chlumant.mon3tize.util.Mon3tizeLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object AuthManager : FirebaseAuthActions {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private suspend fun getGoogleIdToken(context: Context): String? {
        Mon3tizeLogger.d("AuthManager", "Starting getGoogleIdToken...")

        val credentialManager = CredentialManager.create(context)
        Mon3tizeLogger.d("AuthManager", "CredentialManager instance created.")

            //todo: nevim jestli tu je ta vyjimka spravny reseni
        val clientId = Mon3tize.clientId ?:
                throw IllegalStateException("OAuth Client ID is not set. Please provide it in Mon3tizeConfiguration.")


        val signInWithGoogleOption = GetSignInWithGoogleOption
            .Builder(clientId)
            .build()

        Mon3tizeLogger.d("AuthManager", "GetSignInWithGoogleOption configured.")

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        Mon3tizeLogger.d("AuthManager", "GetCredentialRequest built.")

        return try {
            val result: GetCredentialResponse = credentialManager.getCredential(context, request)
            Mon3tizeLogger.d("AuthManager", "Credential response received: ${result.credential}")

            val credential = result.credential
            Mon3tizeLogger.d("AuthManager", "Credential type: ${credential::class.simpleName}")

            if (credential is GoogleIdTokenCredential) {
                Mon3tizeLogger.d("AuthManager", "Google ID Token found.")
                return credential.idToken
            }

            if (credential is androidx.credentials.CustomCredential) {
                Mon3tizeLogger.d("AuthManager", "CustomCredential detected, attempting to convert...")
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    Mon3tizeLogger.d("AuthManager", "Extracted Google ID Token: $idToken")
                    return idToken
                } catch (e: Exception) {
                    Mon3tizeLogger.e("AuthManager", "Failed to convert CustomCredential: ${e.message}")
                }
            }

            Mon3tizeLogger.e("AuthManager", "Unexpected credential type: ${credential::class.simpleName}")
            null
        } catch (e: Exception) {
            Mon3tizeLogger.e("AuthManager", "Error during Google Credential request: ${e.message}")
            null
        }
    }

    override suspend fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                Mon3tizeLogger.d("AuthManager", "Starting signInWithGoogle...")

                val idToken = getGoogleIdToken(context)
                Mon3tizeLogger.d("AuthManager", "ID Token received: $idToken")

                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken, onResult)
                } else {
                    Mon3tizeLogger.e("AuthManager", "ID Token is null.")
                    withContext(Dispatchers.Main) {
                        onResult(false, null)
                    }
                }
            }
        } catch (e: Exception) {
            Mon3tizeLogger.e("AuthManager", "Log In Failed: ${e.message}")
            withContext(Dispatchers.Main) {
                onResult(false, null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        Mon3tizeLogger.d("AuthManager", "Attempting Firebase sign-in with Google ID Token...")
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    Mon3tizeLogger.d("AuthManager", "User Signed In: $uid")
                    onResult(true, uid)
                } else {
                    Mon3tizeLogger.e("AuthManager", "Firebase Authentication Failed: ${task.exception?.message}")
                    onResult(false, null)
                }
            }
    }

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUid(): String? {
        return auth.currentUser?.uid
    }

    override fun getEmail(): String? {
        return auth.currentUser?.email
    }

    override fun isAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }
}

