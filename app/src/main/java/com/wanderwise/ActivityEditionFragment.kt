package com.wanderwise
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ActivityEditionFragment: Fragment(R.layout.activity_activity_edition){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityEditionButton: Button = view.findViewById(R.id.activity_button)
        activityEditionButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_edit_activity)
        }
    }
}