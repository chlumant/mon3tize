package cz.cvut.fit.chlumant.demoApp.ui.components

import android.app.Activity
import android.content.Context
import android.widget.Toast

fun showToast(context: Context?, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}
