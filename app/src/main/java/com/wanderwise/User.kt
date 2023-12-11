package com.wanderwise

import android.os.Parcel
import android.os.Parcelable

import com.google.firebase.database.FirebaseDatabase

data class User(var userId: String = "",
                var name: String = "",
                var email: String = "",
                var password: String = "",
                var permissionLvl: String = "") : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(permissionLvl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel?): User {
            return User(parcel!!)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls<User?>(size)
        }
    }
}
