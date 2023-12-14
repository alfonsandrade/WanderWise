package com.wanderwise

import android.util.Log
import com.google.firebase.database.*
import android.widget.ListView

// Firebase module to encapsulate Firebase-related logic
class Firebase {
    private var tripTable: DatabaseReference = FirebaseDatabase.getInstance().
    getReferenceFromUrl("FirebaseReference").
    child("trips")
    private var cityTable: DatabaseReference = FirebaseDatabase.getInstance().
    getReferenceFromUrl("FirebaseReference").
    child("cities")
    private var attractionTable: DatabaseReference = FirebaseDatabase.getInstance().
    getReferenceFromUrl("FirebaseReference").
    child("attractions")

    fun storeTripToFirebase(trip: Trip) {
        tripTable.child(trip.tripId).setValue(trip.toFirebaseTrip())
    }

    fun storeCityToFirebase(city: City) {
        cityTable.child(city.cityId).setValue(city.toFirebaseCity())
    }

    fun storeAttractionToFirebase(attraction: Attraction) {
        attractionTable.child(attraction.attractionId).setValue(attraction.toFirebaseAttraction())
    }

    fun loadTripsFromFirebase(listView: ListView, tripList: ArrayList<Trip>) {
        tripTable.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripList.clear()
                for (snapshot in dataSnapshot.children) {
                    val fTrip = snapshot.getValue(FirebaseTrip::class.java)
                    if (null != fTrip) {
                        val trip = Trip.fromFirebaseTrip(fTrip)
                        tripList.add(trip)
                    } else {
                        println("Error: could not get FirebaseTrip from database")
                    }
                }
                listView.adapter = TripAdapter(listView.context, tripList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TripSelectionScreen", "Firebase Database Error: ${databaseError.message}")
            }
        })
    }

    fun loadCitiesOfTrip(tripId: String, listView: ListView, cityList: ArrayList<City?>) {
        cityTable.orderByChild("tripId").equalTo(tripId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cityList.clear()
                for (snapshot in dataSnapshot.children) {
                    val city = snapshot.getValue(City::class.java)
                    city?.let { cityList.add(it) }
                }
                listView.adapter = CityAdapter(listView.context, cityList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CitySelectionFragment", "Firebase Database Error: ${databaseError.message}")
            }
        })
    }

    fun loadAttractionsOfCity(cityId: String, listView: ListView, attractionList: ArrayList<Attraction>) {
        attractionTable.orderByChild("cityId").equalTo(cityId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                attractionList.clear()
                for (snapshot in dataSnapshot.children) {
                    val attraction = snapshot.getValue(Attraction::class.java)
                    attraction?.let { attractionList.add(it) }
                }
                listView.adapter = AttractionAdapter(listView.context, attractionList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("AttractionSelectionFragment", "Firebase Database Error: ${databaseError.message}")
            }
        })
    }
}