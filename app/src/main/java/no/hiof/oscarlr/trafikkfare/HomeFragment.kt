package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*
import no.hiof.oscarlr.trafikkfare.HomeFragmentArgs.Companion.fromBundle

class HomeFragment : Fragment() {

    companion object {
        private const val SOMETHING_WENT_WRONG = "Something went wrong"
    }

    private lateinit var usernameTextView : TextView
    private lateinit var username : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            usernameTextView = textViewUsername
            username = fromBundle(requireArguments()).username
            usernameTextView.text = username

            bottom_navigation.setOnNavigationItemReselectedListener {
                navigateToDistOf(it)
            }
        }
    }

    private fun navigateToDistOf(item: MenuItem) {
        when (item.itemId) {
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
    }

    private fun navigateToHome() {
        val usernameFromTextView = textViewUsername.text.toString()

        if (usernameFromTextView.isNotEmpty()) {
            val action = HomeFragmentDirections.actionSeeHome(usernameFromTextView)
            findNavController().navigate(action)
        }
        else
            Toast.makeText(context, SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show()

        Log.d("MainActivity", "Navigated to home")
    }

    private fun navigateToDangers() {
        val usernameFromTextView = textViewUsername.text.toString()

        if (usernameFromTextView.isNotEmpty()) {
            val action = HomeFragmentDirections.actionSeeDangers(usernameFromTextView)
            findNavController().navigate(action)
        }
        else
            Toast.makeText(context, SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show()
        Log.d("MainActivity", "Navigated to dangers")
    }

    private fun navigateToMap() {
        val intent = Intent(activity, MapActivity::class.java)
        startActivity(intent)
        Log.d("MapActivity", "Navigated to map")
    }
}