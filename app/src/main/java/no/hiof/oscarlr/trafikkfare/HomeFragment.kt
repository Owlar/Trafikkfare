package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var supportFragmentManager : FragmentManager

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
                    Toast.makeText(it.context, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        }
    }
}