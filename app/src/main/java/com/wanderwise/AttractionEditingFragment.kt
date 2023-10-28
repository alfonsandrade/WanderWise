package com.wanderwise
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class AttractionEditingFragment: Fragment(R.layout.activity_attraction_editing){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityEditingButton: Button = view.findViewById(R.id.activity_editing_button)
        activityEditingButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_edit_attraction)
        }
    }
}