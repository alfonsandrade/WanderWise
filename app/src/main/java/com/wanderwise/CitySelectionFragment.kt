package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate

class CitySelectionFragment : Fragment(R.layout.activity_city_selection){
    private var trip: Trip= Trip(
        "Japan trip",
        LocalDate.of(2023, 6, 12),
        LocalDate.of(2023, 6, 29),
        "A trip to Japan",
        ArrayList(),
        R.drawable.landscape
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addCityBtn: Button = view.findViewById(R.id.addCityBtn)
        val tripBriefBtn: Button = view.findViewById(R.id.tripBriefBtn)
        val listView: ListView = view.findViewById(R.id.citiesListView)

        try {
            val bundle: Bundle = requireArguments()
            treatReceivedData(bundle)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        var city = City("Berlin",
            "3Sterne",
            LocalDate.of(2023, 6, 12),
            LocalDate.of(2023, 6, 19),
            "Urlaub nach Berlin",
            ArrayList())
        trip.cities.add(city)
        city = City("Dresden",
            "Die Urlaub",
            LocalDate.of(2023, 6, 20),
            LocalDate.of(2023, 6, 23),
            "Urlaub nach Dresden",
            ArrayList())
        trip.cities.add(city)

        listView.adapter = CityAdapter(requireContext(), trip.cities)
        listView.isClickable = true

        addCityBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newCity)
        }
        listView.setOnItemClickListener {parent, view, position, id ->
            val bundle: Bundle = bundleOf("city" to trip.cities[position])
            findNavController().navigate(R.id.action_to_attraction_selection, bundle)
        }
        tripBriefBtn.setOnClickListener(){
            val context = requireContext()
            val popupMenu = PopupMenu(context, it)

            popupMenu.setOnMenuItemClickListener(){item ->
                when(item.itemId){
                    R.id.item1 -> {
                        true
                    }
                    R.id.item2 -> {
                        true
                    }
                    R.id.item3 -> {
                        true
                    }
                    R.id.item4 -> {
                        true
                    }
                    R.id.item5 -> {
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.show()
        }
    }

    private fun treatReceivedData(bundle: Bundle) {
        val city = bundle.getParcelable<City>("newCity")
        val arrivingTrip = bundle.getParcelable<Trip>("trip")

        if (null != arrivingTrip) {
            this.trip = arrivingTrip
        } else if (null != city) {
            trip.cities.add(city)
        }
    }
}