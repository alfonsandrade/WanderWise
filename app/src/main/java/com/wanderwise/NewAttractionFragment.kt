package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NewAttractionFragment : Fragment(R.layout.activity_new_attraction) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listActBtn: Button = view.findViewById(R.id.listActivitiesBtn)

        listActBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_attraction_selection)
        }
    }
}