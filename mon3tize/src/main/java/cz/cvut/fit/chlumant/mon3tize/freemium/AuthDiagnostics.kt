package cz.cvut.fit.chlumant.mon3tize.freemium

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public object AuthDiagnostics {

    private const val WEB_CLIENT_ID = "68214838435-fesjfgrps0jcdgts4u5jmdkegnshq2ar.apps.googleusercontent.com"

    public fun listGoogleAccounts(context: Context) {
        val accounts = AccountManager.get(context).accounts
        Log.d("AuthDiagnostics", "Available Accounts:")
        accounts.forEach { account ->
            Log.d("AuthDiagnostics", "Account: ${account.name} - ${account.type}")
        }
    }


    public suspend fun diagnosticCheck(context: Context) {
        Log.d("AuthDiagnostics", "Starting diagnostic check...")

        listGoogleAccounts(context)

        // Check if Firebase Auth is initialized
        val auth = FirebaseAuth.getInstance()
        Log.d("AuthDiagnostics", "Firebase Auth Initialized. Current User: ${auth.currentUser?.email}")

        // Check if user is signed in
        val isUserSignedIn = auth.currentUser != null
        Log.d("AuthDiagnostics", "User Signed In: $isUserSignedIn")

        // Check Web Client ID
        Log.d("AuthDiagnostics", "Expected Web Client ID: $WEB_CLIENT_ID")

        // Check OAuth configuration
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            withContext(Dispatchers.IO) {
                Log.d("AuthDiagnostics", "Requesting Google Credential...")
                val result: GetCredentialResponse = credentialManager.getCredential(context, request)
                Log.d("AuthDiagnostics", "Credential response received: $result")

                val credential = result.credential
                if (credential == null) {
                    Log.e("AuthDiagnostics", "No credential returned in response.")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No credential received.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d("AuthDiagnostics", "Credential type: ${credential::class.simpleName}")
                    if (credential::class.simpleName == "GoogleIdTokenCredential") {
                        Log.d("AuthDiagnostics", "GoogleIdTokenCredential found.")
                    } else {
                        Log.e("AuthDiagnostics", "Credential is not of type GoogleIdTokenCredential.")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthDiagnostics", "Error during credential check: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Check if user is test user
        val isTestUser = isTestUser()
        Log.d("AuthDiagnostics", "Is Test User: $isTestUser")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Diagnostic completed. Check LogCat for details.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isTestUser(): Boolean {
        val testUsers = listOf("tondachlum27@gmail.com") // <-- Add other test users here
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email
        return testUsers.contains(userEmail)
    }
}