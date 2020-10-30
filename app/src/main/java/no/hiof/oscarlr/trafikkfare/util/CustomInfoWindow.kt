package no.hiof.oscarlr.trafikkfare.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import no.hiof.oscarlr.trafikkfare.R

class CustomInfoWindow(context: Context) : GoogleMap.InfoWindowAdapter {

    @SuppressLint("InflateParams")
    var markerWindow =
        (context as Activity).layoutInflater.inflate(R.layout.activity_map_custom_info_window, null)!!

    private fun windowText(marker: Marker, view: View) {
        val dangerTitle = view.findViewById<TextView>(R.id.infoWindowTitle)
        val dangerDescription = view.findViewById<TextView>(R.id.infoWindowDescription)
        dangerTitle.text = marker.title
        dangerDescription.text = marker.snippet
        //ImageView src is set in resource file instead of here
    }

    override fun getInfoWindow(marker: Marker?): View {
        if (marker != null) {
            windowText(marker, markerWindow)
        }
        return markerWindow
    }

    override fun getInfoContents(marker: Marker?): View {
        if (marker != null) {
            windowText(marker, markerWindow)
        }
        return markerWindow
    }
}