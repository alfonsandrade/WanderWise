package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.FirebaseDatabase
import java.time.format.DateTimeFormatter

class NewCityFragment : Fragment(R.layout.activity_new_city) {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var selectedHotelName: String? = null // New variable to hold the selected hotel name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn: ImageButton = view.findViewById(R.id.confirm_button)
        val cityName: EditText = view.findViewById(R.id.cityNameText)
        val fromDate: EditText = view.findViewById(R.id.fromDate)
        val toDate: EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)
        val tripId = arguments?.getString("tripId") ?: return

        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                // Use the selectedHotelName for the hotel name
                val newCity = City(
                    "",
                    tripId,
                    cityName.text.toString(),
                    selectedHotelName.orEmpty(),
                    fromDate.text.toString(),
                    toDate.text.toString(),
                    description.text.toString()
                )
                saveCityToFirebase(newCity)
            }
        }

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_hotel_fragment)
                as AutocompleteSupportFragment

        autocompleteFragment.apply {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
            setTypeFilter(TypeFilter.ESTABLISHMENT)
            setHint(getString(R.string.hotel_inn))
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    selectedHotelName = place.name
                }

                override fun onError(status: Status) {
                    // TODO: Handle the error.
                }
            })
        }

        autocompleteFragment.view?.post {
            val autocompleteEditText = autocompleteFragment.view?.findViewById<View>(
                com.google.android.libraries.places.R.id.places_autocomplete_search_input
            ) as? EditText

            val searchIcon = autocompleteFragment.view?.findViewById<View>(
                com.google.android.libraries.places.R.id.search_mag_icon
            )
            searchIcon?.visibility = View.GONE

            val clearButton = autocompleteFragment.view?.findViewById<View>(
                com.google.android.libraries.places.R.id.places_autocomplete_clear_button
            )
            clearButton?.visibility = View.GONE
        }

        view.post {
            customizeAutocompleteFragment(autocompleteFragment)
        }

        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                val newCity = City(
                    "",
                    tripId,
                    cityName.text.toString(),
                    selectedHotelName.orEmpty(),
                    fromDate.text.toString(),
                    toDate.text.toString(),
                    description.text.toString()
                )
                saveCityToFirebase(newCity)
            }
        }
    }

    private fun customizeAutocompleteFragment(fragment: AutocompleteSupportFragment) {
        val fragmentView = fragment.view as? LinearLayout ?: return

        for (i in 0 until fragmentView.childCount) {
            val child = fragmentView.getChildAt(i)
            when (child) {
                is ImageView -> {
                    child.visibility = View.GONE
                }
                is EditText -> {
                    child.apply {
                        setBackgroundResource(R.drawable.edittext_underline)
                        setPadding(12, 12, 12, 12)
                        setHintTextColor(resources.getColor(R.color.light_gray))
                        setTextColor(resources.getColor(R.color.black))
                    }
                }
            }
        }
    }

    private fun saveCityToFirebase(city: City) {
        val database = FirebaseDatabase.getInstance().reference.child("cities")
        val key = database.push().key
        key?.let {
            city.cityId = it
            database.child(key).setValue(city.toFirebaseCity()).addOnCompleteListener { task ->
                if (task.isSuccessful && isAdded) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("newCity", city)
                    findNavController().popBackStack()
                }
            }
        }
    }
}
