package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CitySelectionFragment : Fragment(R.layout.activity_city_selection){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addCityBtn: Button = view.findViewById(R.id.addCityBtn)
        val tripBriefBtn: Button = view.findViewById(R.id.tripBriefBtn)
        val listActBtn: Button = view.findViewById(R.id.justABtn)

        addCityBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_newCity)
        }
        tripBriefBtn.setOnClickListener(){
            val context = requireContext()
            val popupMenu = PopupMenu(context, it)

            popupMenu.setOnMenuItemClickListener(){item ->
                when(item.itemId){
                    R.id.item1 -> {
                        true
                    }
                    R.id.item2 -> {
                        true
                    }
                    R.id.item3 -> {
                        true
                    }
                    R.id.item4 -> {
                        true
                    }
                    R.id.item5 -> {
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.show()
        }
        listActBtn.setOnClickListener(){
            findNavController().navigate(R.id.action_to_activity_selection)
        }
    }
}