package com.wanderwise

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationUtils(private val activity: Activity) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        setupLocationClient()
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    fun getLocation(callback: (Location) -> Unit) {
        try {
            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnSuccessListener { location: Location? ->
                location?.let {
                    callback(it)
                } ?: run {
                    Toast.makeText(activity, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(activity, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
