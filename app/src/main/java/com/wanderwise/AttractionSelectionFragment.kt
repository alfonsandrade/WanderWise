package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class AttractionSelectionFragment : Fragment(R.layout.activity_attraction_selection) {
    private lateinit var cityId: String
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var attractionList: ArrayList<Attraction>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.attractionsListView)
        database = FirebaseDatabase.getInstance().reference.child("attractions")

        val city: City = arguments?.getParcelable("selectedCity") ?: return
        cityId = city.cityId
        loadAttractionsForCity(cityId)
    }

    private fun loadAttractionsForCity(cityId: String) {
        attractionList = ArrayList()
        database.orderByChild("cityId").equalTo(cityId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                attractionList.clear()
                for (snapshot in dataSnapshot.children) {
                    val attraction = snapshot.getValue(Attraction::class.java)
                    attraction?.let { attractionList.add(it) }
                }
                listView.adapter = AttractionAdapter(requireContext(), attractionList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    /*
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
            findNavController().navigate(R.id.action_to_new_attraction)
        }
    }

    private fun treatReceivedData(bundle: Bundle) {
        val attraction   = bundle.getParcelable<Attraction>("newAttraction")
        val arrivingCity = bundle.getParcelable<City>("city")

        if (null != arrivingCity) {
            city = arrivingCity
        } else if (null != attraction) {
            city.addAttraction(attraction)
        }
    }
     */
}}