package no.hiof.oscarlr.trafikkfare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import no.hiof.oscarlr.trafikkfare.HomeFragmentArgs.Companion.fromBundle

class HomeFragment : Fragment() {

    companion object {
        private lateinit var usernameTextView : TextView
        private lateinit var username : String

        fun getUsernameTextView(): TextView {
            return usernameTextView
        }
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
            usernameTextView = textViewUsername
            username = fromBundle(requireArguments()).username
            usernameTextView.text = username
        }
    }


}