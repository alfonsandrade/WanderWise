package com.wanderwise

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit;

class CitySelectionFragment : Fragment(R.layout.activity_city_selection){
    private lateinit var tripId: String
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var cityList: ArrayList<City?>
    private lateinit var citiesEventListener: ValueEventListener
    private lateinit var locationUtils: LocationUtils
    private lateinit var userPermission: String
    private lateinit var localTrip: Trip

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.citiesListView)
        database = FirebaseDatabase.getInstance().reference.child("cities")
        locationUtils = LocationUtils(requireActivity())
        locationUtils.requestLocationPermission()
        setupListView(view)
        setupAddCityButton(view)
        setupTripBriefButton(view)
        loadCitiesForTripFromArguments()
    }

    private fun setupListView(view: View) {
        listView = view.findViewById(R.id.citiesListView)
        cityList = ArrayList()
        val adapter = CityAdapter(requireContext(), cityList)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cityList[position]
            val bundle = bundleOf(
                "selectedCity" to selectedCity,
                "hotelName" to selectedCity?.hotelName,
                "hotelLat" to selectedCity?.hotelLat,
                "hotelLng" to selectedCity?.hotelLng,
                "userPermission" to userPermission
            )
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

    private fun setupTripBriefButton(view: View) {
        val tripBriefBtn: ImageButton = view.findViewById(R.id.tripBriefBtn)
        tripBriefBtn.setOnClickListener {
            val popup = PopupMenu(requireContext(), tripBriefBtn)
            popup.inflate(R.menu.trip_brief_popup)

            // Fills up the popup menu with the trip's information
            popup.menu.findItem(R.id.tripName).title = (resources.getString(R.string.trip_name_two_dots) + " " + localTrip.name)
            popup.menu.findItem(R.id.tripStartDate).title = (resources.getString(R.string.from_two_dots) + " " + localTrip.fromDate?.format(DATE_FORMAT))
            popup.menu.findItem(R.id.tripEndDate).title = (resources.getString(R.string.to_two_dots) + " " + localTrip.toDate?.format(DATE_FORMAT))

            val duration = ChronoUnit.DAYS.between(localTrip.fromDate, localTrip.toDate)
            popup.menu.findItem(R.id.duration).title = (resources.getString(R.string.duration_two_dots) + " " + duration.toString() + " " + resources.getString(R.string.days))

            popup.show()
        }
    }

    private fun loadCitiesForTripFromArguments() {
        arguments?.getParcelable<Trip>("selectedTrip")?.let {
            localTrip = it
            tripId = it.tripId
            loadCitiesForTrip(tripId)
        } ?: Log.e("CitySelectionFragment", "Trip not found in arguments")

        arguments?.getString("userPermission")?.let {
            userPermission = it
        } ?: Log.e("CitySelectionFragment", "User permission not found in arguments")
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