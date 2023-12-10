package com.wanderwise

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class AttractionEditingFragment : Fragment(R.layout.activity_attraction_editing) {
    private lateinit var attraction: Attraction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val saveButton: ImageButton = view.findViewById(R.id.confirm_button)
        val cancelButton: ImageButton = view.findViewById(R.id.back_button)
        val attractionName: EditText = view.findViewById(R.id.attractionNameEditText)
        val attractionCheckbox: CheckBox = view.findViewById(R.id.attractionCheckbox)

        val attractionArg = arguments?.getParcelable<Attraction>("selectedAttraction")
        if (attractionArg != null) {
            attraction = attractionArg
            attractionName.setText(attraction.name)
            attractionCheckbox.isChecked = attraction.getIsChecked()

            saveButton.setOnClickListener {
                attraction.name = attractionName.text.toString()
                attraction.setIsChecked(attractionCheckbox.isChecked)
                saveAttractionToFirebase(attraction)
            }
        }else {
            Log.e("AttractionEditingFragment", "Attraction not found in arguments")
        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_attraction_selection)
        }
    }

    fun saveAttractionToFirebase(attraction: Attraction) {
        val database = FirebaseDatabase.getInstance().reference.child("attractions")
        database.child(attraction.attractionId).setValue(attraction.toFirebaseAttraction()).addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("updatedAttraction", attraction)
                findNavController().navigate(R.id.action_to_attraction_selection)
            }
        }
    }
}
