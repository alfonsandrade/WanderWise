package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class NewTripFragment : Fragment(R.layout.activity_new_trip) {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn:   ImageButton = view.findViewById(R.id.confirm_button)
        val tripName:    EditText = view.findViewById(R.id.tripNameText)
        val fromDate:    EditText = view.findViewById(R.id.fromDate)
        val toDate:      EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)
        val cancelButton:   ImageButton = view.findViewById(R.id.back_button)
        val userId:      String   = arguments?.getString("userId") ?: return

        submitBtn.isClickable = true
        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                val newTrip = Trip(
                    "",
                    userId,
                    tripName.text!!.toString(),
                    LocalDate.parse(fromDate.text!!.toString(), DATE_FORMAT),
                    LocalDate.parse(toDate.text!!.toString(), DATE_FORMAT),
                    description.text!!.toString()
                )

                saveTripToFirebase(newTrip)
            }
        }

        cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveTripToFirebase(trip: Trip) {
        val database = FirebaseDatabase.getInstance().reference.child("trips")
        val key = database.push().key
        key?.let {
            trip.tripId = it
            database.child(key).setValue(trip.toFirebaseTrip()).addOnCompleteListener { task ->
                if (task.isSuccessful && isAdded) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("newTrip", trip)
                    findNavController().popBackStack()
                }
            }
        }
    }
}