package com.wanderwise

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CityAdapter(private val context: Context, private val arrayList: ArrayList<City?>) :
    ArrayAdapter<City?>(context, R.layout.city_list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false)
        val city = arrayList[position]

        view.findViewById<TextView>(R.id.city_name).text = city?.name ?: "Unknown"
        val fromDate = city?.fromDateStr ?: "N/A"
        val toDate = city?.toDateStr ?: "N/A"
        view.findViewById<TextView>(R.id.from_date).text = fromDate
        view.findViewById<TextView>(R.id.to_date).text = toDate
        return view
    }
}

