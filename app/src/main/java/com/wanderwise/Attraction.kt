package com.wanderwise

import android.os.Parcelable

data class Attraction(var name: String = "", var isChecked: Boolean = false) : Parcelable{

    ///////// Parcelable implementation /////////
    constructor(parcel: android.os.Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attraction> {
        override fun createFromParcel(parcel: android.os.Parcel): Attraction {
            return Attraction(parcel)
        }

        override fun newArray(size: Int): Array<Attraction?> {
            return arrayOfNulls(size)
        }
    }

    ///////// Custom methods /////////

    fun setIsChecked(isChecked: Boolean) {
        this.isChecked = isChecked
    }
    fun getIsChecked(): Boolean {
        return this.isChecked
    }
}
