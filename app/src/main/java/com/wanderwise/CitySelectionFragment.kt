package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CitySelectionFragment : Fragment(R.layout.activity_city_selection){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addCityBtn: Button = view.findViewById(R.id.addCityBtn)
        val listActBtn: Button = view.findViewById(R.id.justABtn)

        addCityBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newCity)
        }
        listActBtn.setOnClickListener(){
            findNavController().navigate(R.id.action_to_activity_selection)
        }
    }
}