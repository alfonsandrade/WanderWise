package com.wanderwise

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.ContentValues
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import android.os.Bundle
import android.os.Build
import android.os.Environment

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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
                    val imageUri = saveBitmapImage(imageBitmap)
                    val storage = FirebaseStorage.getInstance().reference.child("images")
                    storage.child(attraction.attractionId).putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
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

    private fun saveBitmapImage(bitmap: Bitmap) : Uri? {
        val timestamp = System.currentTimeMillis()
        val contentResolver: ContentResolver = requireContext().contentResolver
        var uri: Uri? = null

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {

                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    contentResolver.update(uri, values, null, null)

                    Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }
        } else {
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {

                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {

            }
        }

        return uri
    }
}