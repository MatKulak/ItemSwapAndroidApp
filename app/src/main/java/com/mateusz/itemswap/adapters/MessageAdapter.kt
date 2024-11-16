package com.mateusz.itemswap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.message.MessageResponse
import com.mateusz.itemswap.data.user.UserResponse

class MessageAdapter(
    private val messages: List<MessageResponse>,
    private val userResponse: UserResponse?
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageChipLeft: Chip = view.findViewById(R.id.messageChipLeft)
        val messageChipRight: Chip = view.findViewById(R.id.messageChipRight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val isSentByUser = message.senderId == userResponse?.id

        if (isSentByUser) {
            holder.messageChipRight.text = message.content
            holder.messageChipLeft.visibility = View.GONE
            holder.messageChipRight.visibility = View.VISIBLE
        } else {
            holder.messageChipLeft.text = message.content
            holder.messageChipLeft.visibility = View.VISIBLE
            holder.messageChipRight.visibility = View.GONE
        }
    }

    override fun getItemCount() = messages.size
}