package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

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

    companion object {

        private class AsyncTaskWorker(private val splashScreenMapActivity: SplashScreenMapActivity) : AsyncTask<Void, String, Boolean>() {

            override fun doInBackground(vararg voids: Void?): Boolean {
                publishProgress("Retrieving data..")

                // Load data here

                publishProgress("..data has been retrieved")
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                if (result!!)
                    splashScreenMapActivity.startMapActivity()

            }

            override fun onProgressUpdate(vararg values: String?) {
                print(values[0])
            }

        }
    }

}