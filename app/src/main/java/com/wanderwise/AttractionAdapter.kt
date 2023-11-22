package com.wanderwise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class AttractionAdapter(context: Context, private val attractions: ArrayList<Attraction>) :
    ArrayAdapter<Attraction>(context, R.layout.attraction_list_item, attractions) {

    private class ViewHolder {
        lateinit var attractionName: TextView
        lateinit var attractionCheckbox: CheckBox
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.attraction_list_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.attractionName = view.findViewById(R.id.attractionName)
            viewHolder.attractionCheckbox = view.findViewById(R.id.checkBox)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val attraction = attractions[position]
        if (attraction != null) {
            viewHolder.attractionName.text = attraction.name
            viewHolder.attractionCheckbox.isChecked = attraction.getIsChecked()

            viewHolder.attractionCheckbox.setOnClickListener {
                attraction.setIsChecked(viewHolder.attractionCheckbox.isChecked)
                // Optionally, update Firebase here if needed
            }
        }

        return view ?: LayoutInflater.from(context).inflate(R.layout.attraction_list_item, parent, false)
    }
}
