package cz.cvut.fit.chlumant.mon3tize.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

public object Dialogs {
    @Composable
    public fun TrialAlreadyUsedDialog(
        onDismiss: () -> Unit,
        onGoToSubscription: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Free Trial Already Used")
            },
            text = {
                Text(text = "You have already used your free trial." +
                        " Gain access by buying a premium subscription.")
            },
            confirmButton = {
                Button(
                    onClick = onGoToSubscription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Buy Subscription")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    public fun TrialExpiredDialog(
        onDismiss: () -> Unit,
        onGoToSubscription: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Free Trial Expired")
            },
            confirmButton = {
                Button(
                    onClick = onGoToSubscription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Buy Subscription")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}