package cz.cvut.fit.chlumant.demoApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import cz.cvut.fit.chlumant.demoApp.ui.AppNavigation
import cz.cvut.fit.chlumant.demoApp.ui.theme.DemoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) { }
        }

        setContent {
            DemoAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}