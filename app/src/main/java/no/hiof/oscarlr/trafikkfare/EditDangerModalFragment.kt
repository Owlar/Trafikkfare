package no.hiof.oscarlr.trafikkfare

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_edit_danger.*
import kotlinx.android.synthetic.main.fragment_edit_danger.view.*
import java.lang.ClassCastException

class EditDangerModalFragment : BottomSheetDialogFragment() {

    private lateinit var editDangerBottomSheetListener : EditDangerBottomSheetListener
    private lateinit var titleText : String
    private lateinit var descriptionText : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_danger, container, false)
        val saveButton = view.findViewById<Button>(R.id.dangerSaveButton)
        setText(view)
        saveButton.setOnClickListener {
            if (editDangerTitle.text.toString().isNotEmpty() && editDangerDescription.text.toString().isNotEmpty()) {
                editDangerBottomSheetListener.saveDangerButtonClicked(
                    editDangerTitle.text.toString(),
                    editDangerDescription.text.toString()
                )
                dismiss()
            }
            else
                Toast.makeText(it.context, "Please fill in title and description", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun setText(view: View) {
        view.editDangerTitle.setText(titleText)
        view.editDangerDescription.setText(descriptionText)
    }

    //Passed from MapActivity
    fun updateVariables(title: String, description: String) {
        titleText = title
        descriptionText = description
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
            throw ClassCastException("$context doesn't implement listener ")
        }

    }
}