package com.wanderwise

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
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
    private lateinit var locationUtils: LocationUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.citiesListView)
        database = FirebaseDatabase.getInstance().reference.child("cities")
        locationUtils = LocationUtils(requireActivity())
        locationUtils.requestLocationPermission()
        setupListView(view)
        setupAddCityButton(view)
        testingLocation(view)
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
        val addCityBtn: ImageButton = view.findViewById(R.id.addCityBtn)
        addCityBtn.setOnClickListener {
            val bundle = bundleOf("tripId" to tripId)
            findNavController().navigate(R.id.action_to_newCity, bundle)
        }
    }

    private fun testingLocation(view: View) {
        val hamburguerBtn: ImageButton = view.findViewById(R.id.tripBriefBtn)
        hamburguerBtn.setOnClickListener {
            locationUtils.getLocation { location ->
                Toast.makeText(requireContext(), "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                Log.d("CitySelectionFragment", "Location: ${location.latitude}, ${location.longitude}")
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                locationUtils.getLocation { location ->
                    Toast.makeText(requireContext(), "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                    Log.d("CitySelectionFragment", "Location: ${location.latitude}, ${location.longitude}")
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(citiesEventListener)
    }
}