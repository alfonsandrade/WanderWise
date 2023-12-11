package com.wanderwise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.core.os.bundleOf
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.format.DateTimeFormatter
import com.google.firebase.database.*

class TripSelectionScreenFragment : Fragment(R.layout.activity_trip_selection) {

    private lateinit var tripList: ArrayList<Trip>
    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var tripsEventListener: ValueEventListener
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wanderwise-firebase-default-rtdb.firebaseio.com/").child("trips")
        tripList = ArrayList()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTripBtn: ImageButton = view.findViewById(R.id.addTripBtn) ?: return
        listView = view.findViewById(R.id.tripsListView)

        loadTripsForUserFromFirebase()

        listView.adapter = TripAdapter(requireContext(), tripList)
        listView.isClickable = true
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedTrip = tripList[position]
            val bundle = bundleOf("selectedTrip" to selectedTrip)
            findNavController().navigate(R.id.action_to_citySelection, bundle)
        }

        addTripBtn.setOnClickListener {
            val bundle = bundleOf("userId" to userId)
            findNavController().navigate(R.id.action_to_newTrip, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::tripsEventListener.isInitialized) {
            database.removeEventListener(tripsEventListener)
        }
    }
    private fun storeTripToFirebase(trip: Trip) {
        database.child(trip.tripId).setValue(trip.toFirebaseTrip())
    }
    private fun loadTripsForUserFromFirebase() {
        userId = arguments?.getString("userId") ?: return

        tripsEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripList.clear()
                for (snapshot in dataSnapshot.children) {
                    val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                    if (firebaseTrip != null) {
                        val trip = Trip.fromFirebaseTrip(firebaseTrip)
                        tripList.add(trip)
                    } else {
                        Log.e("TripSelectionScreen", "Error parsing FirebaseTrip data")
                    }
                }
                (listView.adapter as? TripAdapter)?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TripSelectionScreen", "Firebase Database Error: ${databaseError.message}")
            }
        }
        database.orderByChild("userId").equalTo(userId).addValueEventListener(tripsEventListener)
    }
}