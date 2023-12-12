package com.wanderwise

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase

class CameraFragment : Fragment(R.layout.activity_camera) {
    private lateinit var attraction: Attraction

    private val CAMERA_REQUEST_CODE = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val attractionArg = arguments?.getParcelable<Attraction>("selectedAttraction")
        if (attractionArg != null) {
            attraction = attractionArg
        }

        while (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 1)
        }

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    // Loads the image taken by the camera into the attraction instance in the database
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val database = FirebaseDatabase.getInstance().reference.child("attractions")
                    database.child(attraction.attractionId).setValue(attraction.toFirebaseAttraction()).addOnCompleteListener { task ->
                        if (task.isSuccessful && isAdded) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("newAttraction", attraction)
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
}