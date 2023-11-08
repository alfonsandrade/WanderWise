package com.wanderwise

import android.content.Context
import android.widget.ArrayAdapter
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView

class AttractionAdapter(private val context: Context, private val arrayList: ArrayList<Attraction?>) :
        ArrayAdapter<Attraction>(context, R.layout.attraction_list_item, arrayList){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.attraction_list_item, null)

            val attractionName: TextView = view.findViewById(R.id.attractionName)
            val attractionCheckbox: CheckBox = view.findViewById(R.id.checkBox)

            attractionName.text = arrayList[position]!!.name
            attractionCheckbox.isChecked = arrayList[position]!!.getIsChecked()

            attractionCheckbox.setOnClickListener {
                arrayList[position]!!.setIsChecked(attractionCheckbox.isChecked)
            }

            return view
        }
}