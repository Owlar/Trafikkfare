package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import no.hiof.oscarlr.trafikkfare.db.Firestore

@Suppress("DEPRECATION")
class SplashScreenMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_main)

        GlobalScope.launch {
            delay(1000L)
            startMainActivity()
        }
        Firestore.getTheNews()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}