package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TripSelectionScreenFragment : Fragment(R.layout.activity_trip_selection) {

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private lateinit var viewModel: TripViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)

        val addTripBtn: Button = view.findViewById(R.id.addTripBtn)
        val listView: ListView = view.findViewById(R.id.tripsListView)

        try {
            val bundle: Bundle = requireArguments()
            treatReceivedData(bundle)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        // Observe changes in the LiveData of trips
        viewModel.trips.observe(viewLifecycleOwner) { tripList ->
            listView.adapter = TripAdapter(requireContext(), tripList)
        }

        listView.isClickable = true
        listView.setOnItemClickListener { parent, view, position, id ->
            findNavController().navigate(R.id.action_to_citySelection)
        }

        addTripBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newTrip)
        }
    }

    private fun treatReceivedData(bundle: Bundle) {
        val trip = bundle.getParcelable<Trip>("newTrip")
        trip?.let {
            viewModel.addTrip(it)
        }
    }
}
