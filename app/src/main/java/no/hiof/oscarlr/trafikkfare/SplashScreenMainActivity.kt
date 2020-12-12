package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.hiof.oscarlr.trafikkfare.db.Firestore

@Suppress("DEPRECATION")
class SplashScreenMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_main)

        GlobalScope.launch(Dispatchers.IO) {
            delay(4000L) //To test
            Firestore.getTheNews()
        }
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}