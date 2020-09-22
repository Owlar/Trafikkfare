package no.hiof.oscarlr.trafikkfare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/* Sources:
https://github.com/googlemaps/android-samples/blob/master/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/BasicMapDemoActivity.kt
*/
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    val HALDEN = LatLng(59.12478, 11.38754)
    val ZOOM_LEVEL = 13f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment : SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(HALDEN))
        }
    }


}