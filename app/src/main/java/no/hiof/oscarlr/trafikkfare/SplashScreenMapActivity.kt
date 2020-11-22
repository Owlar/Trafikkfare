package no.hiof.oscarlr.trafikkfare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import no.hiof.oscarlr.trafikkfare.model.GasStation
import no.hiof.oscarlr.trafikkfare.model.GasStation.Companion.gasStations
import no.hiof.oscarlr.trafikkfare.model.jsonModel.StatensVegvesen
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

    fun startMapActivity() {
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncTaskWorker(private val splashScreenMapActivity: SplashScreenMapActivity) : AsyncTask<Void, String, String>() {

        override fun doInBackground(vararg voids: Void?): String {

            createGasStationsFromJsonFile()

            var resultString = ""
            try {
                val url = URL("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=vegvesen&inputtype=textquery&fields=formatted_address,name&locationbias=circle:5000@59.12478,11.38754-122.2226413&key=AIzaSyAQVriOk4Bjy4dpNl9oR0w25WP60uphBi8")
                val httpUrlConnection = url.openConnection() as HttpURLConnection

                httpUrlConnection.readTimeout = 8000
                httpUrlConnection.connectTimeout = 8000
                httpUrlConnection.doOutput = true
                httpUrlConnection.connect()

                val responseCode: Int = httpUrlConnection.responseCode
                Log.d("Activity", "Response is $responseCode")

                if (responseCode == 200) {
                    val inputStream: InputStream = httpUrlConnection.inputStream
                    val isReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(isReader)

                    var temporaryString: String?

                    try {
                        while (true) {
                            temporaryString = bufferedReader.readLine()
                            if (temporaryString == null)
                                break
                            resultString += temporaryString
                        }
                    } catch (e: Exception) {
                        Log.e("Activity", "Error: ${e.printStackTrace()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("", "Error in doInBackground: ${e.message}")
            }

            return resultString
        }

        override fun onPostExecute(result: String?) {
            result ?: return

            if (result == "") {
                Log.e("Activity", "Network error")
            }
            else {
                val gasStationData : StatensVegvesen = Gson().fromJson(result, StatensVegvesen::class.java)
                val outputJson : String = Gson().toJson(gasStationData)

                // Create StatensVegvesen object here


                Log.d("Gas Stations", gasStations.toString())
                Log.d("Activity", outputJson)

                splashScreenMapActivity.startMapActivity()
            }
        }

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

}