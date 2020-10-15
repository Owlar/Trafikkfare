package no.hiof.oscarlr.trafikkfare

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_danger_list.*
import kotlinx.android.synthetic.main.fragment_home.*
import no.hiof.oscarlr.trafikkfare.HomeFragmentArgs.Companion.fromBundle
import no.hiof.oscarlr.trafikkfare.adapter.DangerAdapter
import no.hiof.oscarlr.trafikkfare.adapter.NewsAdapter
import no.hiof.oscarlr.trafikkfare.model.News

class HomeFragment : Fragment() {

    companion object {
        private const val HOME_TITLE_NO = "Hjem"

        private lateinit var usernameTextView : TextView
        private lateinit var username : String

        fun getUsernameTextView(): TextView {
            return usernameTextView
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private var news : ArrayList<News> = News.getNews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity is EditBars)
            (activity as EditBars).setBarTitle(HOME_TITLE_NO)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            usernameTextView = textViewUsername
            username = fromBundle(requireArguments()).username
            usernameTextView.text = username
        }
        setupRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView() {
        homeFragment_newsRecyclerView.adapter = NewsAdapter(news)
        homeFragment_newsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

}