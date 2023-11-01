package com.wanderwise

import android.os.Parcelable
import java.time.LocalDate

data class City(var name: String = "",
                var hotelName: String = "",
                var fromDate: LocalDate?,
                var toDate: LocalDate?,
                var description: String = "",
                var attractions: ArrayList<Attraction?>) : Parcelable {

    ///////// Parcelable implementation /////////
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readSerializable() as? LocalDate,
        parcel.readSerializable() as? LocalDate,
        parcel.readString()!!,
        parcel.readArrayList(Attraction::class.java.classLoader) as ArrayList<Attraction?>
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(hotelName)
        parcel.writeSerializable(fromDate)
        parcel.writeSerializable(toDate)
        parcel.writeString(description)
        parcel.writeList(attractions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: android.os.Parcel): City {
            return City(parcel)
        }

        override fun newArray(size: Int): Array<City?> {
            return arrayOfNulls<City?>(size)
        }
    }

    ///////// Custom methods /////////

    fun addAttraction(attraction: Attraction) {
        this.attractions.add(attraction)
    }
}
