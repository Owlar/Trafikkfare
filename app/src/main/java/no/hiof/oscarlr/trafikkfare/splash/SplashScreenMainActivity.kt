package no.hiof.oscarlr.trafikkfare.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import no.hiof.oscarlr.trafikkfare.MainActivity
import no.hiof.oscarlr.trafikkfare.R

@Suppress("DEPRECATION")
class SplashScreenMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_main)

        Handler().postDelayed({

            doWork()

        }, 500)

    }

    private fun doWork() {

        // Work to be done

        // When work is done
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}