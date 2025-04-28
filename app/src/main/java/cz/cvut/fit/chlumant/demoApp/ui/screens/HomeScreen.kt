package cz.cvut.fit.chlumant.demoApp.ui.screens

import android.app.Activity
import android.util.Log
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

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import cz.cvut.fit.chlumant.demoApp.ui.components.*
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

import cz.cvut.fit.chlumant.mon3tize.adManagers.InterstitialAdManager
import cz.cvut.fit.chlumant.mon3tize.components.AdBanner

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val interstitialAdManager = remember { InterstitialAdManager(context as Activity, UserKeys.AdMob.INTERSTITIAL_DEMO) }

    val manager = Mon3tize.freemiumManager

    LaunchedEffect(Unit) {
        val user = Firebase.auth.currentUser
        val isFreemium = manager.isFreemiumCurrentlyActive()
        Log.d(
            "AUTH",
            "Uživatel: ${user?.email ?: "anonymní"} | Freemium aktivní: $isFreemium"
        )
    }


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
            NavigationButton(navController, "Sign Out", "signout")
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
            NavigationButton(navController, "Resetovat Trial", "resettrial")
        }
    }
}

