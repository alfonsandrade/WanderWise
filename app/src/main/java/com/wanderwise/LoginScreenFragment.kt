package com.wanderwise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import androidx.navigation.fragment.findNavController

class LoginScreenFragment : Fragment(R.layout.activity_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val loginButton: Button = view.findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_tripSelection)
        }
    }
}

