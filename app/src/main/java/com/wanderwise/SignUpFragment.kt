package com.wanderwise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

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
        val permissionEditText: EditText = view.findViewById(R.id.permission_edit_text)
        val signUpButton: ImageButton = view.findViewById(R.id.confirm_register_button)
        val backToLoginButton: ImageButton = view.findViewById(R.id.back_button)

        signUpButton.setOnClickListener {
            if (nameEditText.text.toString().isNotEmpty() &&
                emailEditText.text.toString().isNotEmpty() &&
                passwordEditText.text.toString().isNotEmpty() &&
                permissionEditText.text.toString().isNotEmpty()) {
                val newUser = User(
                    "",
                    nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    permissionEditText.text.toString()
                )
                saveUserToFirebase(newUser)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            backToLoginButton.setOnClickListener {
                findNavController().navigate(R.id.action_to_login_screen)
            }
        }
    }

    private fun saveUserToFirebase(user: User) {
        val database = FirebaseDatabase.getInstance().reference.child("users")
        val key = database.push().key
        key?.let {
            user.userId = it
            database.child(key).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful && isAdded) {
                    findNavController().popBackStack()
                }
            }
        }
    }
}
