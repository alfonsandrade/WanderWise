package com.wanderwise

import android.os.Bundle
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

        fun loadTripsFromFirebase() {
            tripsEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    tripList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                        firebaseTrip?.let {
                            tripList.add(Trip.fromFirebaseTrip(it))
                        }
                    }
                    (listView.adapter as TripAdapter).notifyDataSetChanged()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            }
            database.addValueEventListener(tripsEventListener)
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTripBtn: Button = view.findViewById(R.id.addTripBtn) ?: return
        listView = view.findViewById(R.id.tripsListView)

        if (!isTripsLoaded) {

            fun loadTripsFromFirebase() {
                tripsEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        tripList.clear()
                        for (snapshot in dataSnapshot.children) {
                            val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                            firebaseTrip?.let {
                                tripList.add(Trip.fromFirebaseTrip(it))
                            }
                        }
                        (listView.adapter as TripAdapter).notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors.
                    }
                }
                database.addValueEventListener(tripsEventListener)
            }

            isTripsLoaded = true
        }

        try {
            val bundle: Bundle = requireArguments()
            treatReceivedData(bundle)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
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
        val trip = bundle.getParcelable<Trip>("newTrip")

        if (null != trip) {
            tripList.add(trip)
            storeTripToFirebase(trip)
        }
    }
    private fun loadTripsFromFirebase() {
        tripsEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripList.clear()
                for (snapshot in dataSnapshot.children) {
                    val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                    firebaseTrip?.let {
                        tripList.add(Trip.fromFirebaseTrip(it))
                    }
                }
                (listView.adapter as TripAdapter).notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        }
        database.addValueEventListener(tripsEventListener)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (this::tripsEventListener.isInitialized) {
            database.removeEventListener(tripsEventListener)
        }
    }
    private fun storeTripToFirebase(trip: Trip) {
        val tripId = database.push().key
        if (tripId != null) {
            database.child(tripId).setValue(trip.toFirebaseTrip())
        }
    }
}