package cz.cvut.fit.chlumant.demoApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.NavigationButton
import cz.cvut.fit.chlumant.demoApp.viewmodels.FreemiumViewModel
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetTrialScreen(
    navController: NavHostController,
    freemiumViewModel: FreemiumViewModel = viewModel(),
    signInViewModel: SignInViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (signInViewModel.isUserSignedIn()) {
                Button(
                    onClick = {
                        scope.launch {
                            freemiumViewModel.resetFreeTrial()
                        }
                        navController.navigate("home")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Reset Free Trial")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate("home")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Back To Home Screen")
                }
            } else {
                Text(text = "You need to be signed in to perform this action.")
                Spacer(modifier = Modifier.height(24.dp))
                NavigationButton(navController, "Sign In", "signin")
            }
        }
    }
}