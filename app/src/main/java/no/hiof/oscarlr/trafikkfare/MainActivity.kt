package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), EditBars {

    companion object {
        private const val SOMETHING_WENT_WRONG = "Something went wrong"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        top_app_bar?.setNavigationOnClickListener {
            Log.d("MainActivity", "Navigate")
        }
        top_app_bar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.profile -> {
                    true
                }
                R.id.search -> {
                    true
                }
                R.id.more -> {
                    true
                }
                else -> false
            }
        }
        val navController = findNavController(R.id.nav_host_fragment)
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_dest -> {
                    navigateToHome()
                }
                R.id.dangers_dest -> {
                    navigateToDangers()
                }
                R.id.map_dest -> {
                    navigateToMap()
                }
            }
            true
        }
    }

    private fun navigateToHome() {
        val usernameFromTextView = HomeFragment.getUsernameTextView().text.toString()

        if (usernameFromTextView.isNotEmpty()) {
            val action = HomeFragmentDirections.actionSeeHome(usernameFromTextView)
            findNavController(R.id.nav_host_fragment).navigate(action)
        }
        else
            Toast.makeText(this, SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show()

        Log.d("MainActivity", "Navigated to home")
    }

    private fun navigateToDangers() {
        val usernameFromTextView = HomeFragment.getUsernameTextView().text.toString()

        if (usernameFromTextView.isNotEmpty()) {
            val action = HomeFragmentDirections.actionSeeDangers(usernameFromTextView)
            this.findNavController(R.id.nav_host_fragment).navigate(action)
        }
        else
            Toast.makeText(this, SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show()
        Log.d("MainActivity", "Navigated to dangers")
    }

    private fun navigateToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        Log.d("MapActivity", "Navigated to map")
    }

    override fun hideBars(isShown: Boolean) {
        if (isShown) {
            top_app_bar.visibility = View.INVISIBLE
            bottom_navigation.visibility = View.INVISIBLE
        } else {
            top_app_bar.visibility = View.VISIBLE
            bottom_navigation.visibility = View.VISIBLE
        }
    }

    override fun setBarTitle(title: String) {
        if (title.isNotEmpty())
            top_app_bar.title = title
    }

}