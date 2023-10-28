package com.wanderwise

import android.os.Parcelable
import java.time.LocalDate

data class Trip(var name: String = "", var fromDate: LocalDate?, var toDate: LocalDate?, var description: String = "",
                var imageId: Int = R.drawable.landscape) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readSerializable() as? LocalDate,
        parcel.readSerializable() as? LocalDate,
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
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
    }
}
