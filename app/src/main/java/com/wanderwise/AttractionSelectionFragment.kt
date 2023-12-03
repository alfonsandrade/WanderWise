package com.wanderwise

import android.os.Bundle
import androidx.core.os.bundleOf
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import androidx.navigation.fragment.findNavController

class AttractionSelectionFragment : Fragment(R.layout.activity_attraction_selection) {
    private lateinit var cityId: String
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var attractionList: ArrayList<Attraction>
    private lateinit var attractionsEventListener: ValueEventListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.attractionsListView)
        database = FirebaseDatabase.getInstance().reference.child("attractions")

        setupListView(view)
        loadAttractionsForCityFromArguments()

        val addNewAttractionButton: Button = view.findViewById(R.id.addActivityBtn)
        addNewAttractionButton.setOnClickListener {
            val bundle = bundleOf("cityId" to cityId)
            findNavController().navigate(R.id.action_to_new_attraction, bundle)
        }
    }

    private fun setupListView(view: View) {
        listView = view.findViewById(R.id.attractionsListView)
        attractionList = ArrayList()
        val adapter = AttractionAdapter(requireContext(), attractionList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAttraction = attractionList[position]
            val bundle = bundleOf("selectedAttraction" to selectedAttraction)
            findNavController().navigate(R.id.action_to_edit_attraction, bundle)
        }
    }


    private fun loadAttractionsForCityFromArguments() {
        arguments?.getParcelable<City>("selectedCity")?.let {
            cityId = it.cityId
            loadAttractionsForCity(cityId)
        }
    }

    private fun loadAttractionsForCity(cityId: String) {
        attractionsEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                attractionList.clear()
                dataSnapshot.children.mapNotNullTo(attractionList) { it.getValue(Attraction::class.java) }
                (listView.adapter as? AttractionAdapter)?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        }
        database.orderByChild("cityId").equalTo(cityId).addValueEventListener(attractionsEventListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(attractionsEventListener)
    }
}
