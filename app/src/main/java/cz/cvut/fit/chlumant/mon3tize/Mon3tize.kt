package cz.cvut.fit.chlumant.mon3tize

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp

object Mon3tize {

    private var configuration: Mon3tizeConfiguration? = null

    @SuppressLint("StaticFieldLeak")
    lateinit var freemiumManager: FreemiumManager
        private set

    fun setUp(configuration: Mon3tizeConfiguration, context: Context) {
        this.configuration = configuration
        this.freemiumManager = FreemiumManager(context.applicationContext)

        //zpracovat vstupni parametry
        this.freemiumManager.isFreemiumEnabled

        validateAdMobManifestConfig(context)
    }

    val isFreemiumSupported: Boolean
        get() = configuration?.enableFreemium == true

    @Composable
    fun LockedContent(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val freemium by freemiumManager.isFreemiumEnabled.collectAsState(initial = false)
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
