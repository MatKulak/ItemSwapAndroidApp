package com.mateusz.itemswap.ztest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R

class StringAdapter(private val strings: MutableList<String>) : RecyclerView.Adapter<StringAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_string, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = strings[position]
    }

    override fun getItemCount(): Int {
        return strings.size
    }

    // Method to add new items to the adapter
    fun addItems(newStrings: List<String>) {
        val startPosition = strings.size
        strings.addAll(newStrings) // Add new strings to the existing list
        notifyItemRangeInserted(startPosition, newStrings.size) // Notify the adapter about the new items
    }
}
