package com.wanderwise

import java.time.LocalDate

data class City(var name: String = "",
                var hotelName: String = "",
                var fromDate: LocalDate?,
                var toDate: LocalDate?,
                var attractions: ArrayList<Attraction>) {

}
