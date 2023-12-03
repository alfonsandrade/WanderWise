package com.wanderwise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class CitySelectionFragment : Fragment(R.layout.activity_city_selection) {
    private lateinit var tripId: String
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var cityList: ArrayList<City?>
    private lateinit var citiesEventListener: ValueEventListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.citiesListView)
        database = FirebaseDatabase.getInstance().reference.child("cities")
        setupListView(view)
        setupAddCityButton(view)
        loadCitiesForTripFromArguments()
    }

    private fun setupListView(view: View) {
        listView = view.findViewById(R.id.citiesListView)
        cityList = ArrayList()
        val adapter = CityAdapter(requireContext(), cityList)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = cityList[position]
            val bundle = bundleOf("selectedCity" to selectedCity)
            findNavController().navigate(R.id.action_to_attraction_selection, bundle)
        }
    }

    private fun setupAddCityButton(view: View) {
        val addCityBtn: Button = view.findViewById(R.id.addCityBtn)
        addCityBtn.setOnClickListener {
            val bundle = bundleOf("tripId" to tripId)
            findNavController().navigate(R.id.action_to_newCity, bundle)
        }
    }

    private fun loadCitiesForTripFromArguments() {
        arguments?.getParcelable<Trip>("selectedTrip")?.let {
            tripId = it.tripId
            loadCitiesForTrip(tripId)
        } ?: Log.e("CitySelectionFragment", "Trip not found in arguments")
    }

    private fun loadCitiesForTrip(tripId: String) {
        database = FirebaseDatabase.getInstance().reference.child("cities")
        citiesEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cityList.clear()
                dataSnapshot.children.mapNotNullTo(cityList) { it.getValue(City::class.java) }
                (listView.adapter as? CityAdapter)?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CitySelectionFragment", "Firebase Database Error: ${databaseError.message}")
            }
        }
        database.orderByChild("tripId").equalTo(tripId).addValueEventListener(citiesEventListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(citiesEventListener)
    }
}