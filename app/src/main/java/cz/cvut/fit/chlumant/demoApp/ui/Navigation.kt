package cz.cvut.fit.chlumant.demoApp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.cvut.fit.chlumant.demoApp.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("detail") { DetailScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("freemium") { FreemiumScreen(navController) }
        composable("freemiumYes") { FreemiumActiveScreen(navController) }
        composable("payment") { PaymentScreen(navController) }
        composable("signin") { SignInScreen(navController) }
        composable("signout") { SignOutScreen(navController) }
        composable("resettrial") { ResetTrialScreen(navController) }
    }
}