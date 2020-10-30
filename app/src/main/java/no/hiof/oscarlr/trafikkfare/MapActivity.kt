package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import no.hiof.oscarlr.trafikkfare.util.CustomInfoWindow
import no.hiof.oscarlr.trafikkfare.util.longToast
import no.hiof.oscarlr.trafikkfare.util.shortSnackbar
import no.hiof.oscarlr.trafikkfare.util.shortToast

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val HALDEN_POSITION = LatLng(59.12478, 11.38754)
        private const val ZOOM_LEVEL_13 = 13f
        private const val MY_POSITION_TITLE = "Her er jeg"
        private const val DEFAULT_DANGER_TITLE = "Fare"
        private const val DEFAULT_DANGER_DESCRIPTION = "Dette er farlig"
    }

    private lateinit var client : FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var marker : Marker
    private lateinit var latLng : LatLng
    private lateinit var mapView : View
    private lateinit var gMap : GoogleMap

    private val fabOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_open) }
    private val fabClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_close) }
    private val fabRotateClockwise : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise) }
    private val fabRotateCounterclockwise : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_counterclockwise) }

    private var isExpanded = false

    private val markerList = mutableListOf<MarkerOptions>()

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
        if (gMap.mapType == MAP_TYPE_NORMAL && switchIsChecked)
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
            it.shortSnackbar("Add danger")
            addMarker(gMap)
        }
        fabMap_delete.setOnClickListener {
            fabMapButtonDeleteClicked(fabClose, fabRotateClockwise)
            it.shortSnackbar("Delete danger")
            deleteMarker(gMap)
        }
    }

    private fun addMarker(gMap: GoogleMap) {
        with(gMap) {
            setOnMapClickListener {
                val markerOptions = MarkerOptions()
                    .title(DEFAULT_DANGER_TITLE)
                    .snippet(DEFAULT_DANGER_DESCRIPTION)
                    .position(it)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.danger))
                marker = addMarker(markerOptions)
                moveCamera(CameraUpdateFactory.newLatLng(it))
                markerList.add(markerOptions)
                setOnMapClickListener{}
            }
            setOnInfoWindowClickListener { editSelectedDanger(marker, gMap) }
        }
    }

    private fun deleteMarker(gMap: GoogleMap) {
        with(gMap) {
            setOnMarkerClickListener { marker ->
                marker.remove() //Remove marker from map
                markerList.forEachIndexed { index, _ ->
                    if (markerList[index].position == marker.position)
                        markerList.removeAt(index)
                }
                setOnMarkerClickListener{false}
                true
            }
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
            setOnMarkerClickListener {
                marker = it
                setOnInfoWindowClickListener { editSelectedDanger(marker, gMap) }
                setOnMarkerClickListener{false}
                true
            }
        }
        gMap.setInfoWindowAdapter(CustomInfoWindow(this))

        val bottomSheet = findViewById<View>(R.id.map_bottomSheet)
        handleBottomSheetSwitches(bottomSheet)
    }

    private fun editSelectedDanger(marker: Marker, gMap: GoogleMap) {
        val view = findViewById<ConstraintLayout>(R.id.editDangerLayout)
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE

            val markerTitle = marker.title
            val markerDescription = marker.snippet

            editDangerTitle.setText(markerTitle)
            editDangerDescription.setText(markerDescription)

            dangerSave.setOnClickListener { saveDanger(view, marker, gMap) }
        }
    }

    private fun saveDanger(view: View, editedmarker: Marker, gMap: GoogleMap) {
        marker.title = editDangerTitle.text.toString()
        marker.snippet = editDangerDescription.text.toString()
        val newDanger = MarkerOptions()
        markerList.forEachIndexed { index, _ ->
            if (markerList[index].position == editedmarker.position) {
                markerList[index] = newDanger
                    .title(marker.title)
                    .snippet(marker.snippet)
            }
        }
        view.visibility = View.GONE
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

