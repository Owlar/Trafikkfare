package no.hiof.oscarlr.trafikkfare

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_edit_danger.*
import java.lang.ClassCastException

class EditDangerModalFragment : BottomSheetDialogFragment() {

    private lateinit var editDangerBottomSheetListener : EditDangerBottomSheetListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_danger, container, false)
        val saveButton = view.findViewById<Button>(R.id.dangerSaveButton)
        saveButton.setOnClickListener {
            editDangerBottomSheetListener.saveDangerButtonClicked(
                editDangerTitle.text.toString(),
                editDangerDescription.text.toString()
            )
            dismiss()
        }

        return view
    }

    interface EditDangerBottomSheetListener {
        fun saveDangerButtonClicked(editedMarkerTitle: String, editedMarkerDescription: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //When opening from MapActivity, and forgetting to implement the interface, exception is thrown
        try {
            editDangerBottomSheetListener = context as EditDangerBottomSheetListener
        } catch (cce: ClassCastException) {
            throw ClassCastException("${context.toString()} doesn't implement listener ")
        }

    }
}