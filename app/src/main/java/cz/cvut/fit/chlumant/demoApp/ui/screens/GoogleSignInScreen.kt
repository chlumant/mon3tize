package cz.cvut.fit.chlumant.demoApp.ui.screens

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.cvut.fit.chlumant.demoApp.ui.components.UserKeys
import cz.cvut.fit.chlumant.demoApp.ui.components.showToast
import cz.cvut.fit.chlumant.demoApp.viewmodels.SignInViewModel
import cz.cvut.fit.chlumant.mon3tize.Mon3tize

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel()
) {
    val context = LocalContext.current

    val webClientId = UserKeys.OAUTH_CLIENT_ID

    val signInClient = remember {
        Mon3tize.freemium.auth.getGoogleSignInClient(context, webClientId)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(
            result,
            onSuccess = { email ->
                navController.popBackStack()
            },
            onError = {
                showToast(context, "Log in failed.")
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
        //TODO: je potreba?
//        Text("Přihlášení do aplikace", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val signInIntent = signInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Sign In With Google")
        }
    }
}