package com.wanderwise

import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    var name: String = "",
    var fromDate: LocalDate? = null,
    var toDate: LocalDate? = null,
    var description: String = "",
    var imageId: Int = R.drawable.landscape
) : Parcelable {

    // Convert the Trip object to a Firebase-friendly format
    fun toFirebaseTrip(): FirebaseTrip {
        return FirebaseTrip(
            name = this.name,
            fromDateStr = this.fromDate?.format(DATE_FORMAT),
            toDateStr = this.toDate?.format(DATE_FORMAT),
            description = this.description,
            imageId = this.imageId
        )
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Convert the Firebase-friendly format back to a Trip object
        fun fromFirebaseTrip(firebaseTrip: FirebaseTrip): Trip {
            val fromDate = firebaseTrip.fromDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }
            val toDate = firebaseTrip.toDateStr?.let { LocalDate.parse(it, DATE_FORMAT) }

            return Trip(
                name = firebaseTrip.name,
                fromDate = fromDate,
                toDate = toDate,
                description = firebaseTrip.description,
                imageId = firebaseTrip.imageId
            )
        }
    }
}

// Define the Firebase-friendly version of the Trip class
data class FirebaseTrip(
    var name: String = "",
    var fromDateStr: String? = null,
    var toDateStr: String? = null,
    var description: String = "",
    var imageId: Int = R.drawable.landscape
)
