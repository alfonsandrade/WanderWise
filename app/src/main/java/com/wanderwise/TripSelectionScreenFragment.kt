package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.firebase.database.*

class TripSelectionScreenFragment : Fragment(R.layout.activity_trip_selection) {

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private lateinit var tripList: ArrayList<Trip>
    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var tripsEventListener: ValueEventListener
    private var isTripsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wanderwise-firebase-default-rtdb.firebaseio.com/").child("trips")
        tripList = ArrayList()
        loadTripsFromFirebase()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTripBtn: Button = view.findViewById(R.id.addTripBtn) ?: return
        listView = view.findViewById(R.id.tripsListView)

        if (!isTripsLoaded) {
            loadTripsFromFirebase()
            isTripsLoaded = true
        }

        listView.adapter = TripAdapter(requireContext(), tripList)
        listView.isClickable = true
        listView.setOnItemClickListener { _, _, position, _ ->
            findNavController().navigate(R.id.action_to_citySelection)
        }

        addTripBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newTrip)
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
            findNavController().navigate(R.id.action_to_citySelection)
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
        if (::tripsEventListener.isInitialized) {
            database.removeEventListener(tripsEventListener)
        }

        tripsEventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newTrips = HashSet<Trip>()
                for (snapshot in dataSnapshot.children) {
                    val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                    if (firebaseTrip != null) {
                        newTrips.add(Trip.fromFirebaseTrip(firebaseTrip))
                    }
                }
                tripList.clear()
                tripList.addAll(newTrips)
                (listView.adapter as TripAdapter).notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading trips!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(tripsEventListener)
    }
    private fun storeTripToFirebase(trip: Trip) {
        val tripId = database.push().key
        if (tripId != null) {
            database.child(tripId).setValue(trip.toFirebaseTrip())
        }
    }
}