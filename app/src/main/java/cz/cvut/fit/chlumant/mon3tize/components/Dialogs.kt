package cz.cvut.fit.chlumant.mon3tize.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Dialogs {
    @Composable
    fun TrialAlreadyUsedDialog(
        onDismiss: () -> Unit,
        onGoToSubscription: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Zkušební doba již byla vyčerpána")
            },
            text = {
                Text(text = "Již jste využili svou bezplatnou zkušební dobu." +
                        " Zakoupením předplatného získáte plný přístup.")
            },
            confirmButton = {
                Button(
                    onClick = onGoToSubscription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Zakoupit předplatné")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Zrušit")
                }
            }
        )
    }
}
