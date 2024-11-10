package com.mateusz.itemswap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.message.MessageResponse

class MessageAdapter(private val messages: List<MessageResponse>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderTextView: TextView = view.findViewById(R.id.senderTextView)
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val usernameString = message.senderUsername + ": "
        holder.senderTextView.text = usernameString
        holder.contentTextView.text = message.content
    }

    override fun getItemCount() = messages.size
}