package no.hiof.oscarlr.trafikkfare

import android.view.View

interface IMainActivity {
    fun hideBars(isShown: Boolean)
    fun setBarTitle(title: String)
    fun closeKeyboard(view: View)
}