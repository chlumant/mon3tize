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
import kotlinx.coroutines.launch
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel

@Composable
fun SignOutScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val isSignedIn = viewModel.isUserSignedIn()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSignedIn) {
                Text("Do you want to sign out?", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.signOut()
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            } else {
                Text("You are currently not signed in.", style = MaterialTheme.typography.headlineSmall)
                NavigationButton(navController, "Sign In", "signin")
                NavigationButton(navController, "Go Back To Home Screen", "home")
            }
        }
    }
}
