package no.hiof.oscarlr.trafikkfare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_danger_list.*
import no.hiof.oscarlr.trafikkfare.adapter.DangerAdapter
import no.hiof.oscarlr.trafikkfare.model.DangerCollectionData

class DangerListFragment : Fragment() {

    private var dangers : ArrayList<DangerCollectionData> = DangerCollectionData.getDangerCollection()

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity is IMainActivity)
            (activity as IMainActivity).setBarTitle("Kolleksjon")
        return inflater.inflate(R.layout.fragment_danger_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        dangerRecyclerView.adapter = DangerAdapter(dangers
        ) { view ->
            val position = dangerRecyclerView.getChildAdapterPosition(view)
            val clickedDanger = dangers[position]
            val action =
                DangerListFragmentDirections.actionDangersToDangerDetailFragment(clickedDanger.uid)
            findNavController().navigate(action)
        }
        dangerRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

}