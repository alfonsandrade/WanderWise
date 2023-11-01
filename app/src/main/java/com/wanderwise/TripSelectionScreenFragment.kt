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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wanderwise-firebase-default-rtdb.firebaseio.com/").child("trips")
        val addTripBtn: Button = view.findViewById(R.id.addTripBtn) ?: return
        listView = view.findViewById(R.id.tripsListView)
        tripList = ArrayList()
        loadTripsFromFirebase()
        if (tripList.isEmpty()) {
            val fromDateStr: String = "12/06/2023"
            val toDateStr: String = "22/06/2023"
            val fromDate: LocalDate = LocalDate.parse(fromDateStr, DATE_FORMAT)
            val toDate: LocalDate = LocalDate.parse(toDateStr, DATE_FORMAT)
            val trip = Trip("Japan trip", fromDate, toDate, "A trip to Japan", R.drawable.landscape)

            tripList.add(trip)

            val fromDateStr2: String = "23/11/2023"
            val toDateStr2: String = "15/12/2023"
            val fromDate2: LocalDate = LocalDate.parse(fromDateStr2, DATE_FORMAT)
            val toDate2: LocalDate = LocalDate.parse(toDateStr2, DATE_FORMAT)
            val trip2 = Trip("Greece trip", fromDate2, toDate2, "A trip to Greece", R.drawable.landscape)

            tripList.add(trip2)

            try {
                val bundle: Bundle = requireArguments()
                treatReceivedData(bundle)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
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

    /**
     * Treats the data received from the previous fragment 
     */
    private fun treatReceivedData(bundle: Bundle) {
        val trip = bundle.getParcelable<Trip>("newTrip")

        if (null != trip) {
            tripList.add(trip)
            storeTripToFirebase(trip)  // Store the received trip to Firebase
        }
    }
    private fun loadTripsFromFirebase() {
        tripsEventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //tripList.clear()
                for (snapshot in dataSnapshot.children) {
                    val firebaseTrip = snapshot.getValue(FirebaseTrip::class.java)
                    if (firebaseTrip != null) {
                        tripList.add(Trip.fromFirebaseTrip(firebaseTrip))
                    }
                }
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