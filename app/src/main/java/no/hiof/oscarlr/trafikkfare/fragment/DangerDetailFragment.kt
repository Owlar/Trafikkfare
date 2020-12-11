package no.hiof.oscarlr.trafikkfare.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.danger_list_item.dangerPosterImageView
import kotlinx.android.synthetic.main.danger_list_item.dangerTitleTextView
import kotlinx.android.synthetic.main.fragment_danger_detail.*
import no.hiof.oscarlr.trafikkfare.DangerDetailFragmentArgs
import no.hiof.oscarlr.trafikkfare.IMainActivity
import no.hiof.oscarlr.trafikkfare.R
import no.hiof.oscarlr.trafikkfare.model.DangerData

class DangerDetailFragment : Fragment() {

    companion object {
        private const val DANGER_TITLE = "Fare"
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity is IMainActivity)
            (activity as IMainActivity).setBarTitle(DANGER_TITLE)
        return inflater.inflate(R.layout.fragment_danger_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments?.let { DangerDetailFragmentArgs.fromBundle(it) }
        val danger = DangerData.getTestDangers()[arguments!!.uid]

        dangerTitleTextView.text = danger.title
        dangerPosterImageView.setImageResource(danger.posterUrl)
        dangerDescriptionTextView.text = danger.description
    }

}