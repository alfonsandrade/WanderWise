package com.wanderwise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class LoginScreenFragment : Fragment(R.layout.activity_login) {
    private lateinit var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference.child("users")

        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val loginButton: Button = view.findViewById(R.id.login_button)
        val signUpButton: Button = view.findViewById(R.id.signUp_button)

        loginButton.setOnClickListener {
            checkUser(view, emailEditText.text.toString(), passwordEditText.text.toString())
        }

        signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup_data_fill)
        }
    }

    // Finds the user using only the email and checks if the password is correct. If so, it changes
    // the fragment to the trip selection screen. If not, it shows a toast with the error.
    private fun checkUser(view: View, email: String, password: String) {
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)

        val query: Query = database.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.iterator().next()
                    if (user.child("password").value == password) {
                        findNavController().navigate(R.id.action_to_tripSelection)
                    }
                    else {
                        passwordEditText.text.clear()
                        Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    emailEditText.text.clear()
                    Toast.makeText(context, "E-mail not registered", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

