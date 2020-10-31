package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_edit_danger.*
import no.hiof.oscarlr.trafikkfare.model.Danger
import no.hiof.oscarlr.trafikkfare.util.longToast
import no.hiof.oscarlr.trafikkfare.util.shortSnackbar
import no.hiof.oscarlr.trafikkfare.util.shortToast

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val HALDEN_POSITION = LatLng(59.12478, 11.38754)
        private const val ZOOM_LEVEL_13 = 13f
        private const val MY_POSITION_TITLE = "Her er jeg"

        //To have something to show upon adding marker to map
        private const val DEFAULT_MARKER_TITLE = "Fare"
        private const val DEFAULT_MARKER_DESCRIPTION = "Beskrivelse av fare"
    }

    private var markerList = mutableListOf<Marker>()

    private lateinit var client : FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var marker : Marker
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

        val firestoreDb = FirebaseFirestore.getInstance()
        val dangersCollectionReference = firestoreDb.collection("dangers")
        generateDangerTestData(dangersCollectionReference)

    }

    private fun generateDangerTestData(dangersCollectionReference: CollectionReference) {
        val dangersTestData = ArrayList<Danger>()

        dangersTestData.add(Danger(1,"Takras", "Ikke parker ved veggen, takras kan forekomme", 1))
        dangersTestData.add(Danger(2, "Kollisjon mellom personbiler", "Vei blokkert grunnet frontkollisjon mellom to personbiler", 2))
        dangersTestData.add(Danger(3,"Glatte veier", "Vær obs på glatte veier", 3))

        dangersTestData.forEach {danger -> dangersCollectionReference.add(danger)}
    }

    private fun handleBottomSheetSwitches(bottomSheet: View?) {
        bottomSheet ?: return
        setTrafficLayer.setOnCheckedChangeListener { _, isChecked ->
            gMap.isTrafficEnabled = isChecked
            if (isChecked) shortToast("Traffic enabled") else shortToast("Traffic disabled")
        }
        setBuildings.setOnCheckedChangeListener { _, isChecked ->
            gMap.isBuildingsEnabled = isChecked
            if (isChecked) shortToast("Buildings enabled") else shortToast("Buildings disabled")
        }
        setTerrainMap.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) shortToast("Terrain Map enabled") else shortToast("Terrain Map disabled")
            MAP_TYPE_TERRAIN.setTerrain(isChecked)
        }
    }

    private fun Int.setTerrain(switchIsChecked: Boolean) {
        if ((gMap.mapType == MAP_TYPE_NORMAL) && switchIsChecked)
            gMap.mapType = this
        else {
            gMap.mapType = MAP_TYPE_NORMAL
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
            it.shortSnackbar("Add danger to the map")
            addMarker()
        }
        fabMap_delete.setOnClickListener {
            fabMapButtonDeleteClicked(fabClose, fabRotateClockwise)
            it.shortSnackbar("Delete danger from the map")
            deleteMarker()
        }
    }

    private fun addMarker() {
        with(gMap) {
            setOnMapClickListener {
                markerOptions = MarkerOptions()
                    .title(DEFAULT_MARKER_TITLE)
                    .snippet(DEFAULT_MARKER_DESCRIPTION)
                    .position(it)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.danger))
                marker = addMarker(markerOptions)
                moveCamera(CameraUpdateFactory.newLatLng(marker.position))

                markerList.add(marker)
                testSeeMarkers()
                setOnMapClickListener{}
            }
            setOnMarkerClickListener {
                setOnInfoWindowClickListener { editSelectedMarker(it) }
                setOnMarkerClickListener {false}
                true
            }
        }
    }

    private fun deleteMarker() {
        with(gMap) {
            setOnMarkerClickListener {
                markerList.remove(it)
                it.remove() //Remove marker from map
                testSeeMarkers()
                setOnMarkerClickListener{false}
                true
            }
        }
    }

    private fun testSeeMarkers() {
        markerList.forEach {
            Log.d("MapActivity", "${it.id} - ${it.title} - ${it.snippet}")
        }
    }

    private fun fabMapButtonAddClicked(fabClose: Animation, fabRotateClockwise: Animation) {
        fabMap_delete.startAnimation(fabClose)
        fabMap_add.startAnimation(fabClose)
        fabMap.startAnimation(fabRotateClockwise)
        isExpanded = false

        removeFabMapButtonContentUserInteraction()
    }

    private fun fabMapButtonDeleteClicked(fabClose: Animation, fabRotateClockwise: Animation) {
        fabMap_delete.startAnimation(fabClose)
        fabMap_add.startAnimation(fabClose)
        fabMap.startAnimation(fabRotateClockwise)
        isExpanded = false

        removeFabMapButtonContentUserInteraction()
    }

    private fun removeFabMapButtonContentUserInteraction() {
        fabMap_add.isClickable = false
        fabMap_add.visibility = View.INVISIBLE
        fabMap_delete.isClickable = false
        fabMap_delete.visibility = View.INVISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        with(gMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN_POSITION, ZOOM_LEVEL_13))
            setOnMapClickListener {
                setOnMarkerClickListener {
                    setOnInfoWindowClickListener { editSelectedMarker(it) }
                    setOnMarkerClickListener {false}
                    true
                }
            }
        }
        val bottomSheet = findViewById<View>(R.id.map_bottomSheet)
        handleBottomSheetSwitches(bottomSheet)
    }

    private fun editSelectedMarker(markerToEdit: Marker) {
        val view = findViewById<ConstraintLayout>(R.id.editDangerLayout)
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE

            val textViewDangerTitle = findViewById<EditText>(R.id.editDangerTitle)
            val textViewDangerDescription = findViewById<EditText>(R.id.editDangerDescription)
            textViewDangerTitle.setText(markerToEdit.title)
            textViewDangerDescription.setText(markerToEdit.snippet)

            marker = markerToEdit

            dangerSaveButton.setOnClickListener { saveMarkerToList(view) }
        }
    }

    private fun saveMarkerToList(editDangerView: View) {
        markerList.forEachIndexed {i, m ->
            if (m == marker) {
                markerList[i] = marker
            }
        }
        if (marker.isInfoWindowShown)
            marker.hideInfoWindow()

        testSeeMarkers()
        updateMapMarker()

        editDangerView.visibility = View.GONE
    }

    private fun updateMapMarker() {
        val textViewDangerTitle = findViewById<EditText>(R.id.editDangerTitle)
        val textViewDangerDescription = findViewById<EditText>(R.id.editDangerDescription)
        marker.title = textViewDangerTitle.text.toString()
        marker.snippet = textViewDangerDescription.text.toString()
    }

    private fun getUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mapFragment.getMapAsync {
                        latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
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
                longToast("Permission to access location denied. Please edit permissions.")
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
        }
    }


}

