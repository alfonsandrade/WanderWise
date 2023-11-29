package com.wanderwise

import android.os.Bundle
import android.util.Log
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
    private lateinit var citiesEventListener: ValueEventListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.citiesListView)
        database = FirebaseDatabase.getInstance().reference.child("cities")

        val addCityBtn: Button = view.findViewById(R.id.addCityBtn) ?: return
        if (addCityBtn.visibility == View.VISIBLE && addCityBtn.isClickable) {
            addCityBtn.setOnClickListener {
                Log.d("CitySelectionFragment", "addCityBtn clicked")
                findNavController().navigate(R.id.action_to_newCity)
            }} else {
            Log.d("CitySelectionFragment", "addCityBtn is not visible or not clickable")
        }

        val trip: Trip = arguments?.getParcelable("selectedTrip") ?: return
        tripId = trip?.tripId ?: return
        loadCitiesForTrip(trip.tripId)
    }

    private fun loadCitiesForTrip(tripId: String) {
        cityList = ArrayList()
        citiesEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cityList.clear()
                for (snapshot in dataSnapshot.children) {
                    val city = snapshot.getValue(City::class.java)
                    city?.let { cityList.add(it) }
                }
                listView.adapter = CityAdapter(requireContext(), cityList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CitySelectionFragment", "Firebase Database Error: ${databaseError.message}")
            }
        }
        database.orderByChild("tripId").equalTo(tripId).addValueEventListener(citiesEventListener)
    }
    private fun storeCityToFirebase(city: City) {
        val key = database.child("cities").push().key
        key?.let {
            city.cityId = key
            database.child(key).setValue(city.toFirebaseCity())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (this::citiesEventListener.isInitialized) {
            database.removeEventListener(citiesEventListener)
        }
    }
}