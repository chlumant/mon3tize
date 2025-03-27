package cz.cvut.fit.chlumant.demoApp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import cz.cvut.fit.chlumant.demoApp.ui.components.*

import cz.cvut.fit.chlumant.mon3tize.adManagers.InterstitialAdManager
import cz.cvut.fit.chlumant.mon3tize.components.AdBanner

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val interstitialAdManager = remember { InterstitialAdManager(context as Activity, UserKeys.AdMob.INTERSTITIAL_DEMO) }

    LaunchedEffect(Unit) {
        interstitialAdManager.loadAd()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AdBanner(UserKeys.AdMob.BANNER_DEMO)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            NavigationButton(navController, "Go to Detail Screen", "detail")
            NavigationButton(navController, "Go to Freemium Screen", "freemium")
            NavigationButton(navController, "Go to Main Screen", "main")
            Button(
                onClick = {
                    interstitialAdManager.showAd {
                        navController.navigate("freemium")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Go to Freemium Screen but with Ad")
            }
        }
    }
}

