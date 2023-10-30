package com.wanderwise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class TripViewModel : ViewModel() {

    val trips: MutableLiveData<ArrayList<Trip>> by lazy {
        MutableLiveData<ArrayList<Trip>>(initializeTrips())
    }

    private fun initializeTrips(): ArrayList<Trip> {
        val tripList = ArrayList<Trip>()

        val fromDate1: LocalDate = LocalDate.of(2023, 6, 12)
        val toDate1: LocalDate = LocalDate.of(2023, 6, 22)
        tripList.add(Trip("Japan trip", fromDate1, toDate1, "A trip to Japan", R.drawable.landscape))

        val fromDate2: LocalDate = LocalDate.of(2023, 11, 23)
        val toDate2: LocalDate = LocalDate.of(2023, 12, 15)
        tripList.add(Trip("Greece trip", fromDate2, toDate2, "A trip to Greece", R.drawable.landscape))

        return tripList
    }

    fun addTrip(trip: Trip) {
        val currentTrips = trips.value ?: ArrayList()
        currentTrips.add(trip)
        trips.postValue(currentTrips)
    }
}
