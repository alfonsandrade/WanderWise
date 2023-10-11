package com.wanderwise

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ActivitySelectionFragment: Fragment(R.layout.activity_activity_selection){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newActBtn: Button = view.findViewById(R.id.addActivityBtn)

        newActBtn.setOnClickListener {
            findNavController().navigate(R.id.action_to_new_activity)
        }
    }
}