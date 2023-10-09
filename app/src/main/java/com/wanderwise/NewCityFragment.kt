package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NewCityFragment : Fragment(R.layout.activity_new_city) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)

        submitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_citySelection)
        }
    }
}