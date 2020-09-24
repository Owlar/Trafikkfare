package no.hiof.oscarlr.trafikkfare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

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
        }
    }
}