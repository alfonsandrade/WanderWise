package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class NewAttractionFragment : Fragment(R.layout.activity_new_attraction) {
    private lateinit var cityId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityId = arguments?.getString("cityId") ?: ""

        val saveButton: ImageButton = view.findViewById(R.id.confirm_button)
        val cancelButton: ImageButton = view.findViewById(R.id.back_button)
        val attractionName: EditText = view.findViewById(R.id.attractionNameEditText)
        val attractionCheckbox: CheckBox = view.findViewById(R.id.attractionCheckbox)

        saveButton.setOnClickListener {
            if (attractionName.text.toString().isNotEmpty()) {
                val newAttraction = Attraction(
                    attractionId = "",
                    cityId = cityId,
                    name = attractionName.text.toString(),
                    isChecked = attractionCheckbox.isChecked
                )
                saveNewAttractionToFirebase(newAttraction)
            }
        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_attraction_selection)
        }
    }

    private fun saveNewAttractionToFirebase(attraction: Attraction) {
        val database = FirebaseDatabase.getInstance().reference.child("attractions")
        val key = database.push().key ?: return
        attraction.attractionId = key

        database.child(key).setValue(attraction.toFirebaseAttraction()).addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("newAttraction", attraction)
                findNavController().popBackStack()
            }
        }
    }
}

