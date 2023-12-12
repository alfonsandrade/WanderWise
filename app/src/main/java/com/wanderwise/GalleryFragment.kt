package com.wanderwise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class GalleryFragment : Fragment(R.layout.activity_camera) {
    private lateinit var attraction: Attraction

    private val REQUEST_CODE_READ_STORAGE = 12
    private val GALLERY_REQUEST_CODE = 1

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val attractionArg = arguments?.getParcelable<Attraction>("selectedAttraction")
        if (attractionArg != null) {
            attraction = attractionArg
        }

        // Request the permission to read external storage
        while (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_READ_STORAGE)
        }

        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent: Intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, GALLERY_REQUEST_CODE)
                } else {
                    // Permission denied, handle the denial
                }
                return
            }
        }
    }

    // Loads the image chosen in the gallery and saves it to the attraction instance in the database
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImage = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImage)
                    val storage = FirebaseStorage.getInstance().reference.child("images")
                    storage.child(attraction.attractionId).putFile(selectedImage!!).addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                            attraction.imageUrl = url.toString()
                            // If image was uploaded, save image url to attraction instance in database
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
    }
}