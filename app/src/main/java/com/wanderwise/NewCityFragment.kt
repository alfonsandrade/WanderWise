package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewCityFragment : Fragment(R.layout.activity_new_city) {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)
        val cityName: EditText = view.findViewById(R.id.cityNameText)
        val hotelName: EditText = view.findViewById(R.id.hotelName)
        val fromDate: EditText = view.findViewById(R.id.fromDate)
        val toDate: EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)
        val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val tripId = arguments?.getString("tripId") ?: return

        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                val newCity = City(
                    "",
                    tripId,
                    cityName.text.toString(),
                    hotelName.text.toString(),
                    fromDate.text.toString(),
                    toDate.text.toString(),
                    description.text.toString()
                )

                saveCityToFirebase(newCity)

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