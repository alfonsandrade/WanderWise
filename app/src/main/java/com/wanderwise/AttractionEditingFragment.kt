package com.wanderwise

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.FirebaseDatabase

class AttractionEditingFragment : Fragment(R.layout.activity_attraction_editing) {
    private lateinit var attraction: Attraction
    private var selectedAttractionCoordinates: LatLng? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attraction = arguments?.getParcelable("selectedAttraction") ?: Attraction()

        val saveButton: ImageButton = view.findViewById(R.id.confirm_button)
        val cancelButton: ImageButton = view.findViewById(R.id.back_button)
        val attractionCheckbox: CheckBox = view.findViewById(R.id.attractionCheckbox)

        attractionCheckbox.isChecked = attraction.isChecked

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_attraction_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Update the attraction details with the selected place
                attraction.name = place.name
                selectedAttractionCoordinates = place.latLng
            }

            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred: $status")
            }
        })
        customizeAutocompleteFragment(autocompleteFragment)

        saveButton.setOnClickListener {
            selectedAttractionCoordinates?.let {
                attraction.latitude = it.latitude
                attraction.longitude = it.longitude
            }
            attraction.isChecked = attractionCheckbox.isChecked

            updateAttractionToFirebase(attraction)
        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_attraction_selection)
        }
    }

    private fun customizeAutocompleteFragment(fragment: AutocompleteSupportFragment) {
        val fragmentView = fragment.view as? LinearLayout ?: return
        for (i in 0 until fragmentView.childCount) {
            when (val child = fragmentView.getChildAt(i)) {
                is EditText -> {
                    child.apply {
                        setHint(R.string.attraction_name_hint)
                        setBackgroundResource(R.drawable.edittext_underline)
                        setPadding(12, 12, 12, 12)
                        setHintTextColor(resources.getColor(R.color.light_gray))
                        setTextColor(resources.getColor(R.color.black))
                    }
                }
            }
        }
    }

    private fun updateAttractionToFirebase(attraction: Attraction) {
        val database = FirebaseDatabase.getInstance().reference.child("attractions")
        database.child(attraction.attractionId).setValue(attraction.toFirebaseAttraction()).addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("updatedAttraction", attraction)
                findNavController().popBackStack()
            }
        }
    }
}
