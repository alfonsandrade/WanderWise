package com.wanderwise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class GuideMeFragment : Fragment(R.layout.activity_guide_me), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var selectedAttraction: Attraction? = null
    private var hotelLatLng: LatLng? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedAttraction = arguments?.getParcelable("selectedAttraction")
        val hotelName = arguments?.getString("hotelName") ?: "Hotel"
        hotelLatLng = arguments?.getParcelable("hotelLatLng") ?: LatLng(-34.0, 151.0)

        view.findViewById<TextView>(R.id.hotelEditText).text = hotelName
        view.findViewById<TextView>(R.id.attractionEditText).text = selectedAttraction?.name
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            selectedAttraction?.let {
                val attractionLatLng = LatLng(it.latitude, it.longitude)
                fetchAndDrawRoute(attractionLatLng)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
    private fun fetchAndDrawRoute(destination: LatLng) {
        val origin = hotelLatLng ?: LatLng(-34.0, 151.0)
        val apiKey = "API_KEY"

        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("GuideMeFragment", "Failed to fetch route data", e)
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("GuideMeFragment", "Response data: $responseData")
                    activity?.runOnUiThread {
                        responseData?.let { processRouteData(it) }
                    }
                }
            }
        })
    }

    private fun processRouteData(jsonData: String) {
        val jsonResponse = JSONObject(jsonData)
        val routes = jsonResponse.getJSONArray("routes")

        if (routes.length() > 0) {
            val points = routes.getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points")

            val line = PolyUtil.decode(points)
            val polylineOptions = PolylineOptions()
                .addAll(line)
                .width(12f)
                .color(resources.getColor(R.color.map_polyline_red))

            activity?.runOnUiThread {
                map.clear()
                map.addPolyline(polylineOptions)
                addMarkers()
                focusMapOnRoute(line)
            }
        } else {
            Log.e("GuideMeFragment", "No routes found")
        }
    }

    private fun addMarkers() {
        hotelLatLng?.let {
            map.addMarker(MarkerOptions().position(it).title("Hotel"))
        }
        selectedAttraction?.let {
            val attractionLatLng = LatLng(it.latitude, it.longitude)
            map.addMarker(MarkerOptions().position(attractionLatLng).title(it.name))
        }
    }

    private fun focusMapOnRoute(routePoints: List<LatLng>) {
        if (routePoints.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            routePoints.forEach { point ->
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}