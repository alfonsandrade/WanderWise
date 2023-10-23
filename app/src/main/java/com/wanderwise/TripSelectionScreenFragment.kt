package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate

class TripSelectionScreenFragment : Fragment(R.layout.activity_trip_selection) {

    private lateinit var tripList: ArrayList<Trip>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTripBtn: Button = view.findViewById(R.id.addTripBtn)
        val listView: ListView = view.findViewById(R.id.tripsListView)

        tripList = ArrayList()
        var fromDate: LocalDate = LocalDate.of(2023, 6, 12)
        var toDate: LocalDate = LocalDate.of(2023, 6, 22)
        var trip = Trip("Japan trip", fromDate, toDate, "A trip to Japan", R.drawable.landscape)
        tripList.add(trip)
        fromDate = LocalDate.of(2023, 11, 23)
        toDate = LocalDate.of(2023, 12, 15)
        trip = Trip("Greece trip", fromDate, toDate, "A trip to Greece", R.drawable.landscape)
        tripList.add(trip)

        listView.adapter = TripAdapter(requireContext(), tripList)
        listView.isClickable = true
        listView.setOnItemClickListener { parent, view, position, id ->
            findNavController().navigate(R.id.action_to_citySelection)
        }

        addTripBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newTrip)
        }
    }
}