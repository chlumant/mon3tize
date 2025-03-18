package cz.cvut.fit.chlumant.demoApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import cz.cvut.fit.chlumant.demoApp.ui.AppNavigation
import cz.cvut.fit.chlumant.demoApp.ui.theme.DemoAppTheme
import cz.cvut.fit.chlumant.demoApp.data.*
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds


class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this) { }

        userPreferences = UserPreferences(applicationContext)

        setContent {
            DemoAppTheme {
                val navController = rememberNavController()
                val isFirstLaunch by userPreferences.isFirstLaunch.collectAsState(initial = null)

                LaunchedEffect(isFirstLaunch) {
                    if (isFirstLaunch == true) {

                        navController.navigate("freemium")
                            lifecycleScope.launch {
                            userPreferences.setFirstLaunch(false)
                        }
                    }
                }
                AppNavigation(navController)
            }
        }
    }
}