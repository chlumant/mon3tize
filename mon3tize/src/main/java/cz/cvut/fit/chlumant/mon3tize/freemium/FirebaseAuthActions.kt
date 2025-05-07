@file:Suppress("DEPRECATION")

package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInClient

public interface FirebaseAuthActions {

    public fun signInWithGoogleToken(idToken: String, onResult: (Boolean, String?) -> Unit)

    public fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient

    public fun signOut()

    // TODO - Přidat další funkce
}