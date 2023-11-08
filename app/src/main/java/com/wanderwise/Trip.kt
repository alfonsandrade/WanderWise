package com.wanderwise

import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Trip(var name: String = "",
                var fromDate: LocalDate?,
                var toDate: LocalDate?,
                var description: String = "",
                var cities: ArrayList<City?>,
                var imageId: Int = R.drawable.landscape) : Parcelable {

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readSerializable() as? LocalDate,
        parcel.readSerializable() as? LocalDate,
        parcel.readString()!!,
        parcel.readArrayList(City::class.java.classLoader) as ArrayList<City?>,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeSerializable(fromDate)
        parcel.writeSerializable(toDate)
        parcel.writeString(description)
        parcel.writeList(cities)
        parcel.writeInt(imageId)
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(parcel: android.os.Parcel): Trip {
            return Trip(parcel)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls<Trip?>(size)
        }

        // Convert the Firebase-friendly format back to a Trip object
        fun fromFirebaseTrip(firebaseTrip: FirebaseTrip): Trip {
            val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fromDate = firebaseTrip.fromDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }
            val toDate = firebaseTrip.toDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }

            return Trip(
                name = firebaseTrip.name,
                fromDate = fromDate,
                toDate = toDate,
                description = firebaseTrip.description,
                cities = firebaseTrip.cities,
                imageId = firebaseTrip.imageId
            )
        }
    }

    ////////// FIREBASE IMPLEMENTATION ///////////

    // Convert the Trip object to a Firebase-friendly format
    fun toFirebaseTrip(): FirebaseTrip {
        return FirebaseTrip(
            name = this.name,
            fromDateStr = this.fromDate?.format(DATE_FORMAT),
            toDateStr = this.toDate?.format(DATE_FORMAT),
            description = this.description,
            cities = this.cities,
            imageId = this.imageId
        )
    }

    fun addCity(city: City) {
        this.cities.add(city)
    }
}

// Define the Firebase-friendly version of the Trip class
data class FirebaseTrip(
    var name: String = "",
    var fromDateStr: String? = null,
    var toDateStr: String? = null,
    var description: String = "",
    var cities: ArrayList<City?>,
    var imageId: Int = R.drawable.landscape
)