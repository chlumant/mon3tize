package cz.cvut.fit.chlumant.demoApp.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel
import cz.cvut.fit.chlumant.mon3tize.AuthManager

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel()
) {
    val context = LocalContext.current

    //Web Client ID (z Google Cloud Console > OAuth Web client)
    val webClientId = "68214838435-fesjfgrps0jcdgts4u5jmdkegnshq2ar.apps.googleusercontent.com"

    val signInClient = remember {
        AuthManager.getGoogleSignInClient(context, webClientId)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(
            result,
            onSuccess = { email ->
                navController.navigate("main")
            },
            onError = {
                Log.e("SignInScreen", "Chyba přihlášení", it)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Přihlášení do aplikace", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val signInIntent = signInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Přihlásit se Google účtem")
        }
    }
}