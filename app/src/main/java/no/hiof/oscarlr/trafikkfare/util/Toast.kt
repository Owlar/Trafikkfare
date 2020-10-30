package no.hiof.oscarlr.trafikkfare.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.shortToast(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun View.shortSnackbar(msg: CharSequence) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
}
