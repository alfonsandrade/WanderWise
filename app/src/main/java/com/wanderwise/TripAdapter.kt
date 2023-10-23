package com.wanderwise

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class TripAdapter(private val context: Context, private val arrayList: ArrayList<Trip>) :
      ArrayAdapter<Trip>(context, R.layout.trip_list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.trip_list_item, null)

        val imageView: ImageView = view.findViewById(R.id.tripImage)
        val tripName: TextView = view.findViewById(R.id.tripName)
        val fromDate: TextView = view.findViewById(R.id.fromDate)
        val toDate: TextView = view.findViewById(R.id.toDate)

        imageView.setImageResource(arrayList[position].imageId)
        tripName.text = arrayList[position].name
        fromDate.text = arrayList[position].fromDate.toString()
        toDate.text = arrayList[position].toDate.toString()

        return view
    }
}