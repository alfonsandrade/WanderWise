package com.wanderwise

import java.time.LocalDate

data class Trip(var name: String = "", var fromDate: LocalDate?, var toDate: LocalDate?, var description: String = "",
                var imageId: Int = R.drawable.landscape){

}
