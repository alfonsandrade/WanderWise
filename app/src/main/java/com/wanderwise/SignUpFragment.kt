package com.wanderwise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_signup_data_fill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText: EditText = view.findViewById(R.id.name_edit_text)
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val signUpButton: ImageButton = view.findViewById(R.id.confirm_register_button)
        val backToLoginButton: ImageButton = view.findViewById(R.id.back_button)

        signUpButton.setOnClickListener {
        }

        backToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_to_login_screen)
        }
    }
}
