package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewTripFragment : Fragment(R.layout.activity_new_trip) {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)
        val tripName: EditText = view.findViewById(R.id.tripNameText)
        val fromDateInput: EditText = view.findViewById(R.id.fromDate)
        val toDateInput: EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)

        submitBtn.setOnClickListener {
            val fromDateString: String = fromDateInput.text.toString()
            val toDateString: String = toDateInput.text.toString()

            val fromLocalDate: LocalDate = LocalDate.parse(fromDateString, DATE_FORMAT)
            val toLocalDate: LocalDate = LocalDate.parse(toDateString, DATE_FORMAT)

            val newTrip = Trip(
                tripName.text.toString(),
                fromLocalDate,
                toLocalDate,
                description.text.toString()
            )

            val bundle: Bundle = bundleOf("newTrip" to newTrip)
            findNavController().navigate(R.id.action_to_tripSelection, bundle)
        }
    }
}
