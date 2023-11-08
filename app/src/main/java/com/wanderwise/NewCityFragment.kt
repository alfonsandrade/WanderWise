package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewCityFragment : Fragment(R.layout.activity_new_city) {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn:   Button   = view.findViewById(R.id.submitBtn)
        val cityName:    EditText = view.findViewById(R.id.cityNameText)
        val hotelName:   EditText = view.findViewById(R.id.hotelName)
        val fromDate:    EditText = view.findViewById(R.id.fromDate)
        val toDate:      EditText = view.findViewById(R.id.toDate)
        val description: EditText = view.findViewById(R.id.descriptionText)

        submitBtn.setOnClickListener {
            if (fromDate.text.toString().isNotEmpty() && toDate.text.toString().isNotEmpty()) {
                val newCity: City = City(
                    cityName.text!!.toString(),
                    hotelName.text!!.toString(),
                    LocalDate.parse(fromDate.text!!.toString(), DATE_FORMAT),
                    LocalDate.parse(toDate.text!!.toString(), DATE_FORMAT),
                    description.text!!.toString(),
                    ArrayList()
                )

                val bundle: Bundle = bundleOf("newCity" to newCity)
                findNavController().navigate(R.id.action_to_citySelection, bundle)
            }
        }
    }
}