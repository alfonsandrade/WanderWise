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
import com.google.firebase.database.*

class CitySelectionFragment : Fragment(R.layout.activity_city_selection) {
    private lateinit var tripId: String
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var cityList: ArrayList<City?>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.citiesListView)
        database = FirebaseDatabase.getInstance().reference.child("cities")
        val trip: Trip = arguments?.getParcelable("selectedTrip") ?: return
        tripId = trip?.tripId ?: return
        loadCitiesForTrip(trip.tripId)
    }

    private fun loadCitiesForTrip(tripId: String) {
        cityList = ArrayList()
        database.orderByChild("tripId").equalTo(tripId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cityList.clear()
                for (snapshot in dataSnapshot.children) {
                    val city = snapshot.getValue(City::class.java)
                    city?.let { cityList.add(it) }
                }
                listView.adapter = CityAdapter(requireContext(), cityList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    /*
    private fun treatReceivedData(bundle: Bundle) {
        val city = bundle.getParcelable<City>("newCity")
        val arrivingTrip = bundle.getParcelable<Trip>("trip")

        if (null != arrivingTrip) {
            this.trip = arrivingTrip
        } else if (null != city) {
            trip.cities.add(city)
        }
    }

     NEED TO IMPLEMENT IN THIS WAY override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
    }*/
}