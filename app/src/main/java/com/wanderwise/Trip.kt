package com.wanderwise

import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.FirebaseDatabase
data class Trip(
    var tripId: String = "",
    var userId: String = "",
    var name: String = "",
    var fromDate: LocalDate?,
    var toDate: LocalDate?,
    var description: String = "",
    var imageId: Int = R.drawable.landscape) : Parcelable {

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readSerializable() as? LocalDate,
        parcel.readSerializable() as? LocalDate,
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(tripId)
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeSerializable(fromDate)
        parcel.writeSerializable(toDate)
        parcel.writeString(description)
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

        fun fromFirebaseTrip(firebaseTrip: FirebaseTrip): Trip {
            val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fromDate = firebaseTrip.fromDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }
            val toDate = firebaseTrip.toDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }

            return Trip(
                tripId = firebaseTrip.tripId,
                userId = firebaseTrip.userId,
                name = firebaseTrip.name,
                fromDate = fromDate,
                toDate = toDate,
                description = firebaseTrip.description,
                imageId = firebaseTrip.imageId
            )
        }
    }

    ////////// FIREBASE IMPLEMENTATION ///////////

    fun toFirebaseTrip(): FirebaseTrip {
        return FirebaseTrip(
            tripId = this.tripId,
            userId = this.userId,
            name = this.name,
            fromDateStr = this.fromDate?.format(DATE_FORMAT),
            toDateStr = this.toDate?.format(DATE_FORMAT),
            description = this.description,
            imageId = this.imageId
        )
    }
}
@IgnoreExtraProperties
data class FirebaseTrip(
    var tripId: String = "",
    var userId: String = "",
    var name: String = "",
    var fromDateStr: String? = null,
    var toDateStr: String? = null,
    var description: String = "",
    var imageId: Int = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tripId" to tripId,
            "userId" to userId,
            "name" to name,
            "fromDateStr" to fromDateStr,
            "toDateStr" to toDateStr,
            "description" to description,
            "imageId" to imageId
        )
    }
    fun Trip.toFirebaseTrip(): FirebaseTrip {
        return FirebaseTrip(
            tripId = this.tripId,
            userId = this.userId,
            name = this.name,
            fromDateStr = this.fromDate?.format(DATE_FORMAT),
            toDateStr = this.toDate?.format(DATE_FORMAT),
            description = this.description,
            imageId = this.imageId
        )
    }

    fun retrieveFromFirebase(tripId: String, callback: (Trip?) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("trips").child(tripId).get().addOnSuccessListener {
            val firebaseTrip = it.getValue(FirebaseTrip::class.java)
            val trip = firebaseTrip?.let { fromFirebaseTrip(it) }
            callback(trip)
        }.addOnFailureListener {
            callback(null)
        }
    }

    companion object {
        val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Convert the Firebase-friendly format back to a Trip object
        fun fromFirebaseTrip(firebaseTrip: FirebaseTrip): Trip {
            val fromDate = firebaseTrip.fromDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }
            val toDate = firebaseTrip.toDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }

            return Trip(
                tripId = firebaseTrip.tripId,
                userId = firebaseTrip.userId,
                name = firebaseTrip.name,
                fromDate = fromDate,
                toDate = toDate,
                description = firebaseTrip.description,
                imageId = firebaseTrip.imageId
            )
        }
        fun saveToFirebase(trip: Trip) {
            val database = FirebaseDatabase.getInstance().reference
            val key = database.child("trips").push().key
            if (key != null) {
                trip.tripId = key
                database.child("trips").child(key).setValue(trip.toFirebaseTrip())
            }
        }
    }
}
