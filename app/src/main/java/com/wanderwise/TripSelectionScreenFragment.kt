package com.wanderwise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
    private var isTripsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wanderwise-firebase-default-rtdb.firebaseio.com/").child("trips")
        tripList = ArrayList()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTripBtn: Button = view.findViewById(R.id.addTripBtn) ?: return
        listView = view.findViewById(R.id.tripsListView)
        val newTrip = arguments?.getParcelable<Trip>("newTrip")
        newTrip?.let {
            if (!tripList.any { trip -> trip.tripId == it.tripId }) {
                storeTripToFirebase(it)
                tripList.add(it)
            }
            arguments?.remove("newTrip")
        }
        try {
            val bundle: Bundle = requireArguments()
            treatReceivedData(bundle)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        if (!isTripsLoaded) {
            loadTripsFromFirebase()
            isTripsLoaded = true
        }
        listView.adapter = TripAdapter(requireContext(), tripList)
        listView.isClickable = true
        listView.setOnItemClickListener { parent, view, position, id ->
            val bundle: Bundle = bundleOf("newTrip" to tripList[position])
            findNavController().navigate(R.id.action_to_citySelection, bundle)
        }

        addTripBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newTrip)
        }
    }

    private fun treatReceivedData(bundle: Bundle) {
        val newTrip = bundle.getParcelable<Trip>("newTrip") ?: return
        if (tripList.none { it.tripId == newTrip.tripId }) {
            database.child(newTrip.tripId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        storeTripToFirebase(newTrip)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
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
    fun loadTripsFromFirebase() {
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
        database.addValueEventListener(tripsEventListener)
    }

}
