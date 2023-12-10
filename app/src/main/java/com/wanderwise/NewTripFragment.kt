package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                val newTripId = UUID.randomUUID().toString()
                val newTrip = Trip(
                    newTripId,
                    tripName.text!!.toString(),
                    LocalDate.parse(fromDate.text!!.toString(), DATE_FORMAT),
                    LocalDate.parse(toDate.text!!.toString(), DATE_FORMAT),
                    description.text!!.toString()
                )

                val bundle: Bundle = bundleOf("newTrip" to newTrip)
                findNavController().navigate(R.id.action_to_tripSelection, bundle)
            }
        }
    }
}