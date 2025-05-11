package cz.cvut.fit.chlumant.demoApp.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.showToast
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel
import cz.cvut.fit.chlumant.mon3tize.freemium.AuthDiagnostics
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        AuthDiagnostics.diagnosticCheck(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            Log.d("SignInScreen", "Sign-In Button Clicked")
            scope.launch {
                Log.d("SignInScreen", "Coroutine started")
                viewModel.handleSignIn(
                    context.applicationContext as Application,
                    onSuccess = { uid ->
                        Log.d("SignInScreen", "Sign In Success: $uid")
                        navController.popBackStack()
                    },
                    onError = {
                        Log.e("SignInScreen", "Sign In Failed: ${it?.message}")
                        showToast(context, "Log in failed.")
                    }
                )
            }
        }) {
            Text("Sign In With Google")
        }
    }
}