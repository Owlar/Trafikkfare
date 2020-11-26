package no.hiof.oscarlr.trafikkfare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import no.hiof.oscarlr.trafikkfare.model.ClosestStatensVegvesen
import no.hiof.oscarlr.trafikkfare.model.GasStation
import no.hiof.oscarlr.trafikkfare.model.GasStation.Companion.gasStations
import no.hiof.oscarlr.trafikkfare.model.gson.StatensVegvesen
import no.hiof.oscarlr.trafikkfare.util.longToast
import no.hiof.oscarlr.trafikkfare.util.shortToast
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class SplashScreenMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_map)

        Handler().postDelayed({
            AsyncTaskWorker(this).execute()
        }, 2000)

    }

    private fun startMapActivity() {
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }

    private fun createGasStationsFromJsonFile() {
        val json : String?
        try {
            val inputStream : InputStream = assets.open("bensinstasjoner.json")
            json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length() - 1) {
                val gasStationJsonObj = jsonArray.getJSONObject(i)

                val gasStation = GasStation(
                    gasStationJsonObj.get("name").toString(),
                    gasStationJsonObj.get("vicinity").toString(),
                    gasStationJsonObj.get("rating").toString(),
                    gasStationJsonObj.get("lat") as Double,
                    gasStationJsonObj.get("lng") as Double
                )

                if (!gasStations.contains(gasStation))
                    gasStations.add(gasStation)
            }
        } catch (ioe : IOException) {
            ioe.printStackTrace()
        }
    }

    private fun proceedAfterInternetWarning() {
        longToast("Permission to access internet denied. Map has limited functionality.")
        startMapActivity()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncTaskWorker(private val splashScreenMapActivity: SplashScreenMapActivity) : AsyncTask<Void, String, String>() {

        override fun doInBackground(vararg voids: Void?): String {

            createGasStationsFromJsonFile()

            val stringBuilder = StringBuilder()

            try {
                val url = URL("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Statens%20Vegvesen&inputtype=textquery&fields=formatted_address,name,opening_hours,geometry&key=AIzaSyAQVriOk4Bjy4dpNl9oR0w25WP60uphBi8")
                val httpUrlConnection = url.openConnection() as HttpURLConnection

                httpUrlConnection.doOutput = true
                httpUrlConnection.connect()

                val responseCode: Int = httpUrlConnection.responseCode
                Log.d("Activity", "Response is $responseCode")

                if (responseCode == 200) {
                    val inputStream: InputStream = httpUrlConnection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                    var temporaryString: String?

                    try {
                        while (true) {
                            temporaryString = bufferedReader.readLine()
                            if (temporaryString == null)
                                break

                            stringBuilder.append(temporaryString)
                        }
                    } catch (e: Exception) {
                        Log.e("Activity", "Error: ${e.printStackTrace()}")
                    }
                }
                httpUrlConnection.disconnect()
            } catch (e: Exception) {
                Log.e("", "Error in doInBackground: ${e.message}")
            }

            return stringBuilder.toString()
        }

        override fun onPostExecute(result: String?) {

            if (result == "") {
                runOnUiThread { proceedAfterInternetWarning() }

            } else {
                val statensVegvesenGson : StatensVegvesen = Gson().fromJson(result, StatensVegvesen::class.java)

                val name = statensVegvesenGson.candidates[0].name
                val address = statensVegvesenGson.candidates[0].formatted_address
                val isOpen = statensVegvesenGson.candidates[0].opening_hours.open_now
                val latitude = statensVegvesenGson.candidates[0].geometry.location.lat
                val longitude = statensVegvesenGson.candidates[0].geometry.location.lng

                val statensVegvesen = ClosestStatensVegvesen(name, address, isOpen, latitude, longitude)
                ClosestStatensVegvesen.closestStatensVegvesen = statensVegvesen

                splashScreenMapActivity.startMapActivity()
            }
        }


    }

}