//package cz.cvut.fit.chlumant.mon3tize
//
//import android.content.Context
//import android.util.Log
//import androidx.credentials.CredentialManager
//import androidx.credentials.GetCredentialRequest
//import androidx.credentials.GetCredentialResponse
//import androidx.credentials.exceptions.GetCredentialException
//import androidx.credentials.playservices.GoogleIdTokenCredential
//import androidx.credentials.playservices.GoogleIdTokenRequestOptions
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class AuthManager(private val context: Context) {
//
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val credentialManager: CredentialManager = CredentialManager.create(context)
//
//    suspend fun signInWithGoogle(): Result<Unit> = withContext(Dispatchers.IO) {
//        try {
//            // Instantiate a Google sign-in request
//            val googleIdOption = GetGoogleIdOption.Builder()
//                // Your server's client ID, not your Android client ID.
//                .setServerClientId(getString(R.string.default_web_client_id))
//                // Only show accounts previously used to sign in.
//                .setFilterByAuthorizedAccounts(true)
//                .build()
//
//// Create the Credential Manager request
//            val request = GetCredentialRequest.Builder()
//                .addCredentialOption(googleIdOption)
//                .build()
//
//            val result: GetCredentialResponse = credentialManager.getCredential(context, request)
//
//            val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
//            val idToken = googleCredential.idToken
//
//            if (idToken.isNullOrEmpty()) {
//                return@withContext Result.failure(IllegalStateException("ID token is null"))
//            }
//
//            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//            auth.signInWithCredential(firebaseCredential).await()
//
//            Result.success(Unit)
//        } catch (e: GetCredentialException) {
//            Log.e("AuthManager", "Credential error: ${e.message}", e)
//            Result.failure(e)
//        } catch (e: Exception) {
//            Log.e("AuthManager", "General sign-in error: ${e.message}", e)
//            Result.failure(e)
//        }
//    }
//}
