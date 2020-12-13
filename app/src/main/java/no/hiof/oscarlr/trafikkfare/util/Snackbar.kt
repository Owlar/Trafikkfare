package no.hiof.oscarlr.trafikkfare.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.shortSnackbar(msg: CharSequence) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
}

fun View.longSnackbar(msg: CharSequence) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
}