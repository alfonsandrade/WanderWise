package com.wanderwise

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Attraction(
    var attractionId: String = "",
    var cityId: String = "",
    var name: String = "",
    var isChecked: Boolean = false
) : Parcelable {

    ///////// Parcelable implementation /////////
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(attractionId)
        parcel.writeString(cityId)
        parcel.writeString(name)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
    ///////// Custom methods /////////

    fun setIsChecked(isChecked: Boolean) {
        this.isChecked = isChecked
    }

    fun getIsChecked(): Boolean {
        return this.isChecked
    }

    ///////// Firebase integration /////////

    // Firebase-friendly format for Attraction
    @Exclude
    fun toFirebaseAttraction(): FirebaseAttraction {
        return FirebaseAttraction(
            attractionId = this.attractionId,
            cityId = this.cityId,
            name = this.name,
            isChecked = this.isChecked
        )
    }

    companion object CREATOR : Parcelable.Creator<Attraction>{
        override fun createFromParcel(parcel: android.os.Parcel): Attraction {
            return Attraction(parcel)
        }
        override fun newArray(size: Int): Array<Attraction?> {
            return arrayOfNulls(size)
        }
        fun fromFirebaseAttraction(firebaseAttraction: FirebaseAttraction): Attraction {
            return Attraction(
                attractionId = firebaseAttraction.attractionId,
                cityId = firebaseAttraction.cityId,
                name = firebaseAttraction.name,
                isChecked = firebaseAttraction.isChecked
            )
        }

        fun saveToFirebase(attraction: Attraction) {
            val database = FirebaseDatabase.getInstance().reference
            val key = database.child("attractions").push().key
            if (key != null) {
                attraction.attractionId = key
                database.child("attractions").child(key).setValue(attraction.toFirebaseAttraction())
            }
        }

        fun retrieveFromFirebase(attractionId: String, callback: (Attraction?) -> Unit) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("attractions").child(attractionId).get().addOnSuccessListener {
                val firebaseAttraction = it.getValue(FirebaseAttraction::class.java)
                val attraction = firebaseAttraction?.let { fromFirebaseAttraction(it) }
                callback(attraction)
            }.addOnFailureListener {
                callback(null)
            }
        }
    }
}

// Firebase-friendly format for Attraction
data class FirebaseAttraction(
    var attractionId: String = "",
    var cityId: String = "",
    var name: String = "",
    var isChecked: Boolean = false
)
