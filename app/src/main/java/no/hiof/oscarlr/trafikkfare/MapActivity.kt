package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    val HALDEN = LatLng(59.12478, 11.38754)
    val ZOOM_LEVEL = 13f

    lateinit var locationManager : LocationManager
    lateinit var location : Location

    private lateinit var map : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment : SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        myLocation.setOnClickListener {
            requestPermissionLocation()
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(HALDEN))
        }
    }

    private fun requestPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), 15715)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        location = locationManager.getLastKnownLocation(GPS_PROVIDER)!!

        var locationListener = LocationListener { p0 -> reverseGeocode(p0) }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 100.2f, locationListener)
    }

    private fun reverseGeocode(loc: Location) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(loc.latitude, loc.longitude, 2)
        val address = addresses[0]

        val toast = Toast.makeText(this, address.latitude.toString() + ", " + address.longitude.toString(), LENGTH_LONG)
        toast.show()

    }
}

