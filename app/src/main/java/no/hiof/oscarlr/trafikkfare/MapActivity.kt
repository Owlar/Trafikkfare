@file:Suppress("DEPRECATION")

package no.hiof.oscarlr.trafikkfare

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map_bottom_sheet.*
import no.hiof.oscarlr.trafikkfare.helper.Firestore
import no.hiof.oscarlr.trafikkfare.model.ClosestStatensVegvesen
import no.hiof.oscarlr.trafikkfare.model.Danger
import no.hiof.oscarlr.trafikkfare.model.GasStation
import no.hiof.oscarlr.trafikkfare.util.longToast
import no.hiof.oscarlr.trafikkfare.util.shortSnackbar
import no.hiof.oscarlr.trafikkfare.util.shortToast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@Suppress("DEPRECATION")
class MapActivity : AppCompatActivity(), OnMapReadyCallback, EditDangerModalFragment.EditDangerBottomSheetListener {

    companion object {
        private val HALDEN_POSITION = LatLng(59.12478, 11.38754)
        private const val ZOOM_LEVEL_13 = 13f
        private const val MY_POSITION_TITLE = "Min posisjon"
        private const val MY_POSITION_DESCRIPTION = "Her befinner jeg meg"

        //To have something to show upon adding marker to map
        private const val DEFAULT_MARKER_TITLE = "Fare"
        private const val DEFAULT_MARKER_DESCRIPTION = "Beskrivelse av fare"

        private const val NO_DANGER = true

        private const val PERMISSION_LOCATION_ID = 11
        private const val CHANNEL_ID = "Channel_21"
        private const val NOTIFICATION_ID = 21
    }

    private var markerList = mutableListOf<Marker>()
    private var createdDangers = ArrayList<Danger>()

    private lateinit var client : FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var marker : Marker
    private lateinit var markerOptions : MarkerOptions
    private lateinit var latLng : LatLng
    private lateinit var mapView : View
    private lateinit var gMap : GoogleMap
    private lateinit var danger : Danger

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

        Firestore.getAllDangers()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Danger", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Dangers on map"
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun dangerNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("New Danger: ${marker.title}")
            .setContentText(marker.snippet)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) //Clear notification after clicking it

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }

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
                setOnMapClickListener{}
            }
            setOnMarkerClickListener {
                setOnInfoWindowClickListener { editSelectedMarker(it) }
                setOnMarkerClickListener { false }
                true
            }
        }
    }

    private fun deleteMarker() {
        with(gMap) {
            setOnMarkerClickListener {
                if (it.tag == NO_DANGER)
                    false
                else {
                    markerList.remove(it)
                    it.remove() //Remove marker from map
                    deleteFromFirestore(it)
                    setOnMarkerClickListener{ false }
                    true
                }
            }
        }
    }

    //START REGION: FOR TESTING PURPOSES
    private fun testSeeMarkers() {
        markerList.forEach {
            Log.d("MapActivity", "${it.id} - ${it.title} - ${it.snippet}")
        }
    }

    private fun testSeeDangers() {
        Danger.getDangers().forEach {
            Log.d("TAG", it.toString())
        }
    }
    //END REGION: FOR TESTING PURPOSES

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
        gMap = googleMap ?: return
        with(gMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(HALDEN_POSITION, ZOOM_LEVEL_13))

            retrieveAndMarkGasStations(gMap)
            retrieveAndMarkStatensVegvesen(gMap)

            val firestoreDb = FirebaseFirestore.getInstance()
            val dangersCollectionReference = firestoreDb.collection("dangers")

            dangersCollectionReference.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val dangerList = ArrayList<Danger>()
                    for (document in it.result) {
                        val danger : Danger = document.toObject(Danger::class.java)
                        dangerList.add(danger)
                    }
                    Danger.setFromFirestore(dangerList)
                    retrieveAndMarkDangers(gMap)
                }
            }
            setOnMarkerClickListener {
                setOnInfoWindowClickListener { editSelectedMarker(it) }
                setOnMarkerClickListener { false }
                true
            }
        }
        val bottomSheet = findViewById<View>(R.id.map_bottomSheet)
        handleBottomSheetSwitches(bottomSheet)
    }

    private fun retrieveAndMarkStatensVegvesen(gMap: GoogleMap) {
        val obj = ClosestStatensVegvesen.closestStatensVegvesen
        markerOptions = MarkerOptions()
            .title("${obj.name} ${isOpenNow(obj)}")
            .snippet(obj.address)
            .position(LatLng(obj.latitude, obj.longitude))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        marker = gMap.addMarker(markerOptions)
        marker.tag = NO_DANGER
    }

    private fun isOpenNow(obj: ClosestStatensVegvesen): String {
        return if (obj.isOpen) "is OPEN" else "is CLOSED"
    }

    private fun retrieveAndMarkGasStations(gMap: GoogleMap) {
        GasStation.gasStations.forEach {
            markerOptions = MarkerOptions()
                .title(it.title)
                .snippet(it.vicinity)
                .position(LatLng(it.latitude, it.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            marker = gMap.addMarker(markerOptions)
            marker.tag = NO_DANGER
        }
    }

    private fun retrieveAndMarkDangers(gMap: GoogleMap) {
        Danger.getDangers().forEach {
            markerOptions = MarkerOptions()
                .title(it.title)
                .snippet(it.description)
                .position(LatLng(it.latitude, it.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.danger))
            marker = gMap.addMarker(markerOptions)
            markerList.add(marker)
        }
    }

    private fun editSelectedMarker(markerToEdit: Marker) {
        if (markerToEdit.tag == NO_DANGER)
            shortToast("Can't edit non-dangers")
        else {
            marker = markerToEdit
            showEditDangerDialog()
        }
    }

    private fun showEditDangerDialog() {
        val editDangerModal = EditDangerModalFragment()
        val args = Bundle()
        args.putString("argTitle", marker.title)
        args.putString("argDescription", marker.snippet)
        editDangerModal.arguments = args
        editDangerModal.show(supportFragmentManager, "editDangerModal")
    }

    override fun saveDangerButtonClicked(editedMarkerTitle: String, editedMarkerDescription: String) {
        if (isConnectedToInternet()) {
            marker.title = editedMarkerTitle
            marker.snippet = editedMarkerDescription
            saveMarkerToList()
            addToFirestore()
            dangerNotification()
        } else
            longToast("You must be connected to internet to save a danger")
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

    private fun saveMarkerToList() {
        markerList.forEachIndexed {i, m ->
            if (m == marker) {
                markerList[i] = marker
            }
        }
        if (marker.isInfoWindowShown)
            marker.hideInfoWindow()
    }

    private fun addToFirestore() {
        val dangerUniqueId = marker.id.toString()
        danger = Danger(dangerUniqueId, marker.title, marker.snippet, 1, marker.position.latitude, marker.position.longitude)
        Firestore.setDanger(danger)

        createdDangers.add(danger)
        Danger.setDangers(createdDangers)
    }

    private fun deleteFromFirestore(it: Marker) {
        marker = it
        val dangerUniqueId = marker.id.toString()
        val danger = Danger(dangerUniqueId, it.title, it.snippet, 1, it.position.latitude, it.position.longitude)
        Firestore.deleteDanger(danger)

        createdDangers.remove(danger)
        Danger.setDangers(createdDangers)

    }

    override fun onDestroy() {
        super.onDestroy()
        markerList.clear()
        gMap.clear()
    }

    @AfterPermissionGranted(PERMISSION_LOCATION_ID)
    private fun getUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mapFragment.getMapAsync {
                        latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                            .title(MY_POSITION_TITLE)
                            .snippet(MY_POSITION_DESCRIPTION)
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL_13))
                        it.addMarker(markerOptions)
                    }
                }
            }
        }
        else {
            EasyPermissions.requestPermissions(
                this,
                "In order to show your location, we need permission to access your location",
                PERMISSION_LOCATION_ID,
                ACCESS_FINE_LOCATION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    fun onActionButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.id) {
                R.id.severityMinor ->
                    if (checked)
                        Log.d("Severity", "MINOR")
                R.id.severityMajor ->
                    if (checked)
                        Log.d("Severity", "MAJOR")
                R.id.severityCritical ->
                    if (checked)
                        Log.d("Severity", "CRITICAL")
                R.id.severityCatastrophic ->
                    if (checked)
                        Log.d("Severity", "CATASTROPHIC")
            }
        }
    }


}

