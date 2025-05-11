package cz.cvut.fit.chlumant.mon3tize.freemium

import android.content.Context
import com.google.firebase.auth.FirebaseUser


public interface FirebaseAuthActions {

    public suspend fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit)

    public fun isUserSignedIn(): Boolean

    public fun signOut()

    public fun getCurrentUser(): FirebaseUser?

    public fun getUid(): String?

    public fun getEmail(): String?

    public fun isAnonymous(): Boolean
}