package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.FirebaseDatabase
import com.google.android.libraries.places.api.Places

class NewCityFragment : Fragment(R.layout.activity_new_city){
    private var selectedHotelName: String? = null
    private var selectedHotelCoordinates: LatLng? = null

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
                val newCity = City(
                    "",
                    tripId,
                    cityName.text.toString(),
                    selectedHotelName.orEmpty(),
                    selectedHotelCoordinates?.latitude,
                    selectedHotelCoordinates?.longitude,
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
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setTypeFilter(TypeFilter.ESTABLISHMENT)
            setHint(getString(R.string.hotel_inn))
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    selectedHotelName = place.name
                    selectedHotelCoordinates = place.latLng
                }
                override fun onError(status: Status) {}
            })
        }
        customizeAutocompleteFragment(autocompleteFragment)
    }

    private fun customizeAutocompleteFragment(fragment: AutocompleteSupportFragment) {
        val fragmentView = fragment.view as? LinearLayout ?: return
        for (i in 0 until fragmentView.childCount) {
            when (val child = fragmentView.getChildAt(i)) {
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
        val key = database.push().key ?: return
        city.cityId = key
        val firebaseCity = city.toFirebaseCity()

        database.child(key).setValue(firebaseCity).addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("newCity", city)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error on database save", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
