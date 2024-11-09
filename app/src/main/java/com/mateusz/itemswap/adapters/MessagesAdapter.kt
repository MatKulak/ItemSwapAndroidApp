package com.mateusz.itemswap.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.databinding.ItemMessageBinding

//class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
//
//    private val messages = mutableListOf<String>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MessageViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        holder.bind(messages[position])
//    }
//
//    override fun getItemCount(): Int = messages.size
//
//    fun addMessage(message: String) {
//        messages.add(message)
//        notifyItemInserted(messages.size - 1)  // Scroll to the bottom when a new message is added
//    }
//
//    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(message: String) {
//            binding.messageTextView.text = message
//        }
//    }
//}