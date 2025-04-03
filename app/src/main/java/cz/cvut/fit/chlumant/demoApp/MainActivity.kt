package cz.cvut.fit.chlumant.demoApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope

import cz.cvut.fit.chlumant.demoApp.ui.AppNavigation
import cz.cvut.fit.chlumant.demoApp.ui.theme.DemoAppTheme

import cz.cvut.fit.chlumant.mon3tize.*


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

                //val isFreemiumActive by Mon3tize.isFreemiumActive.collectAsState(initial = false)
                val isFirstLaunch by Mon3tize.isFirstLaunch.collectAsState(initial = null)

                LaunchedEffect(isFirstLaunch) {
                    if (isFirstLaunch == true && Mon3tize.isFreemiumSupported) {
                        navController.navigate("freemium")
                        Mon3tize.setFirstLaunch(false)
                    }
                }

                AppNavigation(navController)
            }
        }
    }
}