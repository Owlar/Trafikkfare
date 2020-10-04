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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    companion object {
        private const val SOMETHING_WENT_WRONG = "Something went wrong"
    }

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
            val usernameTextView: TextView = view.findViewById(R.id.textViewUsername)
            val username = HomeFragmentArgs.fromBundle(requireArguments()).username
            usernameTextView.text = username

            userLocationButton.setOnClickListener {
                val intent = Intent(activity, MapActivity::class.java)
                startActivity(intent)
            }

            dangersButton.setOnClickListener {
                val usernameFromTextView = textViewUsername.text.toString()

                if (usernameFromTextView.isNotEmpty()) {
                    val action = HomeFragmentDirections.actionSeeDangers(usernameFromTextView)
                    it.findNavController().navigate(action)
                }
                else
                    Toast.makeText(it.context, SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show()
            }

            bottom_navigation.setOnNavigationItemReselectedListener {
                navigateToDistOf(it)
            }
        }
    }

    private fun navigateToDistOf(item: MenuItem) {
        when (item.itemId) {
            R.id.home_dest -> {
                findNavController().navigate(R.id.homeFragment)
                Log.d("MainActivity", "Navigated to home")
            }
            R.id.dangers_dest -> {
                findNavController().navigate(R.id.dangers)
            }
            R.id.map_dest -> {
                val intent = Intent(activity, MapActivity::class.java)
                startActivity(intent)
                Log.d("MapActivity", "Navigated to map")
            }
        }
    }
}