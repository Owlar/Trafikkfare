package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
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

    companion object {
        private val HALDEN_POSITION = LatLng(59.12478, 11.38754)

        private const val HALDEN_TITLE = "Halden"
        private const val MY_POSITION_TITLE = "I am here"
        private const val ZOOM_LEVEL_13 = 13f
        private const val ADD_DANGER_TO_MAP = "Click on the map to add a new danger"
        private const val DELETE_DANGER_FROM_MAP = "Click on a danger marker to delete it"
        private const val DEFAULT_DANGER_TITLE = "Fare!"
    }

    private lateinit var client : FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var markerOptions : MarkerOptions
    private lateinit var latLng : LatLng
    private lateinit var mapView : View
    private lateinit var gMap : GoogleMap

    private val fabOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_open) }
    private val fabClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_close) }
    private val fabRotateClockwise : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise) }
    private val fabRotateCounterclockwise : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_counterclockwise) }

    private var isExpanded = false

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
            mapView = view
            fabMapButtonClicked()
        }
    }

    private fun fabMapButtonClicked() {

        if (isExpanded) {
            fabMap_delete.startAnimation(fabClose)
            fabMap_add.startAnimation(fabClose)
            fabMap.startAnimation(fabRotateClockwise)

            isExpanded = false
        } else {
            fabMap_delete.startAnimation(fabOpen)
            fabMap_add.startAnimation(fabOpen)
            fabMap.startAnimation(fabRotateCounterclockwise)

            fabMap_delete.isClickable
            fabMap_add.isClickable

            isExpanded = true
        }
        fabMap_add.setOnClickListener {
            fabMapButtonAddClicked(fabClose, fabRotateClockwise)
            fabMapAddButtonMessage()
            addMarker(gMap)
        }
        fabMap_delete.setOnClickListener {
            fabMapButtonDeleteClicked(fabClose, fabRotateClockwise)
            fabMapDeleteButtonMessage()
            deleteMarker(gMap)
        }
    }

    private fun addMarker(gMap: GoogleMap) {
        with(gMap) {
            setOnMapClickListener {
                addMarker(MarkerOptions().title(DEFAULT_DANGER_TITLE).position(it).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger)))
                moveCamera(CameraUpdateFactory.newLatLng(it))
            }
        }
    }

    private fun deleteMarker(gMap: GoogleMap) {
        with(gMap) {
            setOnMarkerClickListener { marker ->
                marker.remove()
                true
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun fabMapButtonAddClicked(fabClose: Animation, fabRotateClockwise: Animation) {
        fabMap_delete.startAnimation(fabClose)
        fabMap_add.startAnimation(fabClose)
        fabMap.startAnimation(fabRotateClockwise)
        isExpanded = false

        removeFabMapButtonContentUserInteraction()
    }

    @SuppressLint("RestrictedApi")
    private fun fabMapButtonDeleteClicked(fabClose: Animation, fabRotateClockwise: Animation) {
        fabMap_delete.startAnimation(fabClose)
        fabMap_add.startAnimation(fabClose)
        fabMap.startAnimation(fabRotateClockwise)
        isExpanded = false

        removeFabMapButtonContentUserInteraction()
    }

    @SuppressLint("RestrictedApi")
    private fun removeFabMapButtonContentUserInteraction() {
        fabMap_add.isClickable = false
        fabMap_add.visibility = View.INVISIBLE
        fabMap_delete.isClickable = false
        fabMap_delete.visibility = View.INVISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN_POSITION, ZOOM_LEVEL_13))
            addMarker(MarkerOptions().title(HALDEN_TITLE).position(HALDEN_POSITION))
            gMap = this
        }
    }

    private fun fabMapAddButtonMessage() {
        Snackbar.make(mapView, ADD_DANGER_TO_MAP, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    private fun fabMapDeleteButtonMessage() {
        Snackbar.make(mapView, DELETE_DANGER_FROM_MAP, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    private fun getUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mapFragment.getMapAsync {
                        latLng = LatLng(location.latitude, location.longitude)
                        markerOptions = MarkerOptions().position(latLng)
                            .title(MY_POSITION_TITLE)
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL_13))
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
            else {
                Toast.makeText(this, "Permission to access location denied. Please edit permissions.", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
        }

    }
}

