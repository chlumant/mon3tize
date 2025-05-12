package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object AuthManager : FirebaseAuthActions {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private suspend fun getGoogleIdToken(context: Context): String? {
        Log.d("AuthManager", "Starting getGoogleIdToken...")

        val credentialManager = CredentialManager.create(context)
//        val clearRequest = androidx.credentials.ClearCredentialStateRequest()
//        credentialManager.clearCredentialState(clearRequest)
        Log.d("AuthManager", "CredentialManager instance created.")

        val signInWithGoogleOption = GetSignInWithGoogleOption
            //todo; vyhodit hardcoded OAuth klic nekam do pice
            //todo; idealne ho predat knihovne pri inicializaci a nejak si
            //todo; ho posilat
            .Builder("68214838435-fesjfgrps0jcdgts4u5jmdkegnshq2ar.apps.googleusercontent.com")
            .build()

        Log.d("AuthManager", "GetSignInWithGoogleOption configured.")

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        Log.d("AuthManager", "GetCredentialRequest built.")

        return try {
            val result: GetCredentialResponse = credentialManager.getCredential(context, request)
            Log.d("AuthManager", "Credential response received: ${result.credential}")

            val credential = result.credential
            Log.d("AuthManager", "Credential type: ${credential::class.simpleName}")

            if (credential is GoogleIdTokenCredential) {
                Log.d("AuthManager", "Google ID Token found.")
                return credential.idToken
            }

            if (credential is androidx.credentials.CustomCredential) {
                Log.d("AuthManager", "CustomCredential detected, attempting to convert...")
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    Log.d("AuthManager", "Extracted Google ID Token: $idToken")
                    return idToken
                } catch (e: Exception) {
                    Log.e("AuthManager", "Failed to convert CustomCredential: ${e.message}")
                }
            }

            Log.e("AuthManager", "Unexpected credential type: ${credential::class.simpleName}")
            null
        } catch (e: Exception) {
            Log.e("AuthManager", "Error during Google Credential request: ${e.message}")
            null
        }
    }


//    private suspend fun getGoogleIdToken(context: Context): String? {
//        Log.d("AuthManager", "Starting getGoogleIdToken...")
//
//        val credentialManager = CredentialManager.create(context)
//        Log.d("AuthManager", "CredentialManager instance created.")
//
//        // Vytvoříme možnost přihlášení přes Google
//        val signInWithGoogleOption = GetSignInWithGoogleOption
//            .Builder("68214838435-fesjfgrps0jcdgts4u5jmdkegnshq2ar.apps.googleusercontent.com")
//            .build()
//
//        Log.d("AuthManager", "GetSignInWithGoogleOption configured.")
//
//        // Vytvoříme požadavek na přihlášení, ale ne automaticky
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(signInWithGoogleOption)
//            .setPreferImmediatelyAvailableCredentials(false) // Tento flag zajistí, že Bottom Sheet se neobjeví automaticky
//            .build()
//
//        Log.d("AuthManager", "GetCredentialRequest built.")
//
//        return try {
//            val result: GetCredentialResponse = credentialManager.getCredential(context, request)
//            Log.d("AuthManager", "Credential response received: ${result.credential}")
//
//            val credential = result.credential
//            Log.d("AuthManager", "Credential type: ${credential::class.simpleName}")
//
//            // Pokud je credential typu GoogleIdTokenCredential
//            if (credential is GoogleIdTokenCredential) {
//                Log.d("AuthManager", "Google ID Token found directly.")
//                return credential.idToken
//            }
//
//            // Pokud je credential typu CustomCredential
//            if (credential is androidx.credentials.CustomCredential) {
//                Log.d("AuthManager", "CustomCredential detected, attempting to convert...")
//                try {
//                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//                    val idToken = googleIdTokenCredential.idToken
//                    Log.d("AuthManager", "Extracted Google ID Token: $idToken")
//                    return idToken
//                } catch (e: Exception) {
//                    Log.e("AuthManager", "Failed to convert CustomCredential to GoogleIdTokenCredential: ${e.message}")
//                }
//            }
//
//            // Výpis veškerých dat, pokud stále token nelze získat
//            Log.e("AuthManager", "Unexpected credential type: ${credential::class.simpleName}")
//            Log.e("AuthManager", "Credential raw data: ${credential.toString()}")
//            null
//        } catch (e: Exception) {
//            Log.e("AuthManager", "Error during Google Credential request: ${e.message}")
//            null
//        }
//    }

    override suspend fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                Log.d("AuthManager", "Starting signInWithGoogle...")

                val idToken = getGoogleIdToken(context)
                Log.d("AuthManager", "ID Token received: $idToken")

                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken, onResult)
                } else {
                    Log.e("AuthManager", "ID Token is null.")
                    withContext(Dispatchers.Main) {
                        onResult(false, null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Log In Failed: ${e.message}")
            withContext(Dispatchers.Main) {
                onResult(false, null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("AuthManager", "Attempting Firebase sign-in with Google ID Token...")
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    Log.d("AuthManager", "User Signed In: $uid")
                    onResult(true, uid)
                } else {
                    Log.e("AuthManager", "Firebase Authentication Failed: ${task.exception?.message}")
                    onResult(false, null)
                }
            }
    }

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun signOut() {
        auth.signOut()
        Log.d("AuthManager", "User Signed Out.")
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

