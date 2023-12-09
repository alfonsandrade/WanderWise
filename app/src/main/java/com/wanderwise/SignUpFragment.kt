package com.wanderwise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_signup_data_fill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText: EditText = view.findViewById(R.id.name_edit_text)
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val signUpButton: Button = view.findViewById(R.id.sign_up_button)
        val loginButton: Button = view.findViewById(R.id.login_button)

        signUpButton.setOnClickListener {
            // Handle the sign-up logic here
        }

        loginButton.setOnClickListener {
            // Navigate back to the login screen
            findNavController().navigate(R.id.action_to_login_screen)
        }
    }
}
