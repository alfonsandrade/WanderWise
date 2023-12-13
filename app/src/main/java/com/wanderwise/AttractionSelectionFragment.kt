package com.wanderwise

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso

class AttractionSelectionFragment : Fragment(R.layout.activity_attraction_selection) {
    private var cityId: String? = null
    private var userPermission: String? = null
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private var attractionList: ArrayList<Attraction> = ArrayList()
    private var attractionsEventListener: ValueEventListener? = null

    private val CAMERA_PERMISSION_LVL: String = "2"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.attractionsListView)
        database = FirebaseDatabase.getInstance().reference.child("attractions")

        setupListView()
        savedInstanceState?.let { bundle ->
            cityId = bundle.getString("cityId")
            userPermission = bundle.getString("userPermission")
        } ?: loadAttractionsForCityFromArguments()

        val addNewAttractionButton: ImageButton = view.findViewById(R.id.addActivityBtn)
        addNewAttractionButton.setOnClickListener {
            cityId?.let { cityId ->
                val bundle = bundleOf("cityId" to cityId)
                findNavController().navigate(R.id.action_to_new_attraction, bundle)
            }
        }

        val hotelNameEditText: TextView = view.findViewById(R.id.hotelEditText)
        arguments?.getString("hotelName")?.let {
            hotelNameEditText.text = it
        }
    }

    override fun onResume() {
        super.onResume()
        cityId?.let { loadAttractionsForCity(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        cityId?.let { outState.putString("cityId", it) }
        userPermission?.let { outState.putString("userPermission", it) }
    }

    private fun setupListView() {
        val adapter = AttractionAdapter(requireContext(), attractionList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAttraction = attractionList[position]

            if ("" != selectedAttraction.imageUrl) {
                val dialog: Dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.popup_image)

                val image : ImageView = dialog.findViewById(R.id.imageViewPopup)
                Picasso.get().load(selectedAttraction.imageUrl).into(image)

                dialog.show()
            }
        }

        listView.isLongClickable = true
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedAttraction = attractionList[position]
            val popup = PopupMenu(requireContext(), listView)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.takePicture -> {
                        if (userPermission!! >= CAMERA_PERMISSION_LVL) {
                            val bundle = bundleOf("selectedAttraction" to selectedAttraction)
                            findNavController().navigate(R.id.action_to_camera, bundle)
                        } else {
                            Toast.makeText(requireContext(), "Only available for user permission 2.\nYour permission: $userPermission", Toast.LENGTH_LONG).show()
                        }
                    }
                    R.id.addPicture -> {
                        if (userPermission!! >= CAMERA_PERMISSION_LVL) {
                            val bundle = bundleOf("selectedAttraction" to selectedAttraction)
                            findNavController().navigate(R.id.action_to_gallery, bundle)
                        } else {
                            Toast.makeText(requireContext(), "Only available for user permission 2.\nYour permission: $userPermission", Toast.LENGTH_LONG).show()
                        }
                    }
                    R.id.editAttraction -> {
                        val bundle = bundleOf("selectedAttraction" to selectedAttraction)
                        findNavController().navigate(R.id.action_to_edit_attraction, bundle)
                    }
                    R.id.deleteAttraction -> {
                        attractionList.remove(selectedAttraction)
                        (listView.adapter as? AttractionAdapter)?.notifyDataSetChanged()
                        database.child(selectedAttraction.attractionId).removeValue()
                    }
                }
                true
            }
            popup.menuInflater.inflate(R.menu.attraction_popup, popup.menu)
            popup.show()
            true
        }
    }

    private fun loadAttractionsForCityFromArguments() {
        arguments?.getParcelable<City>("selectedCity")?.let {
            cityId = it.cityId
            loadAttractionsForCity(cityId!!)
        }
        arguments?.getString("userPermission")?.let {
            userPermission = it
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
                Log.e("AttractionSelectionFragment", "Database Error: ${databaseError.message}")
            }
        }
        database.orderByChild("cityId").equalTo(cityId).addValueEventListener(attractionsEventListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        attractionsEventListener?.let {
            database.removeEventListener(it)
        }
    }
}

