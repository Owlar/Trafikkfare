package no.hiof.oscarlr.trafikkfare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    companion object {
        private const val INVALID_CREDENTIALS = "Invalid credentials"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            if (editTextUsername.text.toString().isNotEmpty() && editTextPassword.text.toString().isNotEmpty()) {
                val username = editTextUsername.text.toString()
                val action = LoginFragmentDirections.actionLoginToHome(username)
                it.findNavController().navigate(action)
            }
            else
                Toast.makeText(it.context, INVALID_CREDENTIALS, Toast.LENGTH_LONG).show()
        }
    }


}
