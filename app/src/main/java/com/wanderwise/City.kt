package com.wanderwise
import java.time.format.DateTimeFormatter
import android.os.Parcelable
import java.time.LocalDate
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.FirebaseDatabase
data class City(
    var cityId: String = "",
    var tripId: String = "",
    var name: String = "",
    var hotelName: String = "",
    var fromDate: LocalDate?,
    var toDate: LocalDate?,
    var description: String = "",
) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readSerializable() as? LocalDate,
        parcel.readSerializable() as? LocalDate,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(cityId)
        parcel.writeString(tripId)
        parcel.writeString(name)
        parcel.writeString(hotelName)
        parcel.writeSerializable(fromDate)
        parcel.writeSerializable(toDate)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }
    fun toFirebaseCity(): FirebaseCity {
        return FirebaseCity(
            cityId = this.cityId,
            tripId = this.tripId,
            name = this.name,
            hotelName = this.hotelName,
            fromDateStr = this.fromDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            toDateStr = this.toDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            description = this.description
        )
    }
    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: android.os.Parcel): City {
            return City(parcel)
        }

        override fun newArray(size: Int): Array<City?> {
            return arrayOfNulls<City?>(size)
        }
    }
}

@IgnoreExtraProperties
data class FirebaseCity(
    var cityId: String = "",
    var tripId: String = "", // Linking City with its parent Trip
    var name: String = "",
    var hotelName: String = "",
    var fromDateStr: String? = null,
    var toDateStr: String? = null,
    var description: String = "",
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "cityId" to cityId,
            "tripId" to tripId,
            "name" to name,
            "hotelName" to hotelName,
            "fromDateStr" to fromDateStr,
            "toDateStr" to toDateStr,
            "description" to description
        )
    }
    companion object {
        private fun City.toFirebaseCity(): FirebaseCity {
            return FirebaseCity(
                cityId = this.cityId,
                tripId = this.tripId,
                name = this.name,
                hotelName = this.hotelName,
                fromDateStr = this.fromDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                toDateStr = this.toDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                description = this.description
            )
        }
        private fun fromFirebaseCity(firebaseCity: FirebaseCity): City {
            val fromDate = firebaseCity.fromDateStr?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
            val toDate = firebaseCity.toDateStr?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }

            return City(
                cityId = firebaseCity.cityId,
                tripId = firebaseCity.tripId,
                name = firebaseCity.name,
                hotelName = firebaseCity.hotelName,
                fromDate = fromDate,
                toDate = toDate,
                description = firebaseCity.description
            )
        }
        fun saveToFirebase(city: City) {
            val database = FirebaseDatabase.getInstance().reference
            val key = database.child("cities").push().key
            if (key != null) {
                city.cityId = key
                database.child("cities").child(key).setValue(city.toFirebaseCity())
            }
        }
        fun retrieveFromFirebase(cityId: String, callback: (City?) -> Unit) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("cities").child(cityId).get().addOnSuccessListener {
                val firebaseCity = it.getValue(FirebaseCity::class.java)
                val city = firebaseCity?.let { fromFirebaseCity(it) }
                callback(city)
            }.addOnFailureListener {
                callback(null)
            }
        }
    }
}



