package cz.cvut.fit.chlumant.mon3tize

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import android.content.pm.PackageManager
import android.util.Log
import android.annotation.SuppressLint
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

    @SuppressLint("StaticFieldLeak")
    lateinit var freemiumManager: FreemiumManager

    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }

        this.freemiumManager = FreemiumManager(context.applicationContext)
        validateAdMobManifestConfig(context)
    }

    val isFreemiumSupported: Boolean
        get() = configuration?.enableFreemium == true

    val isFreemiumActive: Flow<Boolean>
        get() = freemiumManager.isFreemiumEnabled

    val isFirstLaunch: Flow<Boolean>
        get() = freemiumManager.isFirstLaunch

    suspend fun setFirstLaunch(value: Boolean) {
        freemiumManager.setFirstLaunch(value)
    }

    @Composable
    fun LockedContent(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val freemium by isFreemiumActive.collectAsState(initial = false)
        if (freemium) {
            content()
        }
    }

    private fun validateAdMobManifestConfig(context: Context) {
        val appId = try {
            val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            ai.metaData?.getString("com.google.android.gms.ads.APPLICATION_ID")
        } catch (_: Exception) {
            null
        }

        if (appId.isNullOrBlank()) {
            Log.e("Mon3tize", "AdMob App ID is missing from AndroidManifest.xml")
        } else {
            Log.d("Mon3tize", "AdMob App ID found: $appId")
        }
    }
}
