package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val HALDEN = LatLng(59.12478, 11.38754)
    private val ZOOM_LEVEL = 13f

    private lateinit var client : FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var markerOptions : MarkerOptions
    private lateinit var latLng : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)!!
        mapFragment.getMapAsync(this)

        client = LocationServices.getFusedLocationProviderClient(this)

        myLocation.setOnClickListener {
            getUserPosition()
        }
        fabMap.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(HALDEN))
        }
        googleMap.setOnMapClickListener {
            googleMap.addMarker(MarkerOptions()
                .title("Fare!").position(it).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger))
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))
        }
    }

    private fun getUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mapFragment.getMapAsync {
                        latLng = LatLng(location.latitude, location.longitude)
                        markerOptions = MarkerOptions().position(latLng)
                            .title("I am here")
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
                        it.addMarker(markerOptions)
                    }
                }
            }
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 11)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 11) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
                getUserPosition()
        }
    }
}

