package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NewTripFragment : Fragment(R.layout.activity_new_trip) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn:   Button   = view.findViewById(R.id.submitBtn)
        val tripName:    EditText = view.findViewById(R.id.tripNameText)
        val fromDate:    EditText = view.findViewById(R.id.fromDate)
        val toDate:      EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)

        // Puts all the data into a bundle and sends it to the next fragment
        submitBtn.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("tripName", tripName.text.toString())
            bundle.putString("fromDate", fromDate.text.toString())
            bundle.putString("toDate", toDate.text.toString())
            bundle.putString("description", description.text.toString())

            findNavController().navigate(R.id.action_to_tripSelection, bundle)
        }
    }
}