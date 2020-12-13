package no.hiof.oscarlr.trafikkfare

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.danger_list_item.dangerPosterImageView
import kotlinx.android.synthetic.main.danger_list_item.dangerTitleTextView
import kotlinx.android.synthetic.main.fragment_danger_detail.*
import no.hiof.oscarlr.trafikkfare.ml.TrafficSignAnimalAnalyzer
import no.hiof.oscarlr.trafikkfare.model.DangerCollectionData

class DangerDetailFragment : Fragment() {

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity is IMainActivity)
            (activity as IMainActivity).setBarTitle("Fare")
        return inflater.inflate(R.layout.fragment_danger_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments?.let { DangerDetailFragmentArgs.fromBundle(it) }
        val danger = DangerCollectionData.getDangerCollection()[arguments!!.uid]

        dangerTitleTextView.text = danger.title
        dangerPosterImageView.setImageResource(danger.posterUrl)
        dangerDescriptionTextView.text = danger.description

        useTrafficSignAnalyzerOnPoster(danger.posterUrl)
    }

    private fun useTrafficSignAnalyzerOnPoster(image: Int) {
        val bitMap = BitmapFactory.decodeResource(context?.resources, image)
        TrafficSignAnimalAnalyzer.labelImage(bitMap)
    }

}