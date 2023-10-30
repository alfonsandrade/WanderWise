package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate

class AttractionSelectionFragment: Fragment(R.layout.activity_attraction_selection){
    private var city: City = City("Belina",
                                "Serj",
                                LocalDate.of(2023, 11, 23),
                                LocalDate.of(2023, 11, 28),
                                ArrayList())
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newAttrBtn: Button = view.findViewById(R.id.addActivityBtn)
        val hotelBtn: Button = view.findViewById(R.id.hotelBtn)
        var hotelIcon: ImageView = view.findViewById(R.id.hotelIcon)
        var hotelBtnText: TextView = view.findViewById(R.id.hotelBtnText)
        val listView: ListView = view.findViewById(R.id.attractionsListView)

        try {
            val bundle: Bundle = requireArguments()
            treatReceivedData(bundle)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        var attraction = Attraction("Uchuaia", false)
        city.addAttraction(attraction)
        attraction = Attraction("Arromeno Glaciers", false)
        city.addAttraction(attraction)

        if (city.hotelName != "") {
            hotelIcon.setImageResource(R.drawable.hotel_icon)
            hotelBtnText.text = city.hotelName
        }

        listView.adapter = AttractionAdapter(requireContext(), city.attractions)
        listView.isClickable = true

        newAttrBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_new_attraction)
        }
        hotelBtn.setOnClickListener {
            
        }
    }

    private fun treatReceivedData(bundle: Bundle) {
        val attraction   = bundle.getParcelable<Attraction>("newAttraction")
        val arrivingCity = bundle.getParcelable<City>("city")

        if (null != arrivingCity) {
            city = arrivingCity
        } else if (null != attraction) {
            city.addAttraction(attraction)
        } else {
            city = City("Belina",
                "Serj",
                LocalDate.of(2023, 11, 23),
                LocalDate.of(2023, 11, 28),
                ArrayList())
        }
    }
}