package com.wanderwise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CityAdapter(private val context: Context, private val arrayList: ArrayList<City?>) :
    ArrayAdapter<City?>(context, R.layout.city_list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.city_list_item, null)

        val tripName: TextView = view.findViewById(R.id.cityName)
        val fromDate: TextView = view.findViewById(R.id.fromDate)
        val toDate: TextView = view.findViewById(R.id.toDate)

        tripName.text = arrayList[position]!!.name
        fromDate.text = arrayList[position]!!.fromDate.toString()
        toDate.text = arrayList[position]!!.toDate.toString()

        return view
    }
}