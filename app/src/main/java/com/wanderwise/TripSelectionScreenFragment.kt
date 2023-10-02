package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wanderwise.R
class TripSelectionScreenFragment : Fragment(R.layout.activity_trip_selection) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tripButton: Button = view.findViewById(R.id.trip_button)
        tripButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_citySelection)
        }
    }
}