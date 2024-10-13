package com.mateusz.itemswap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.AdvertisementWithFilesResponse
//import com.mateusz.itemswap.model.AdvertisementWithFilesResponse
import com.mateusz.itemswap.databinding.ItemAdvertisementBinding

class AdvertisementAdapter : RecyclerView.Adapter<AdvertisementAdapter.AdvertisementViewHolder>() {

    private val advertisementList = mutableListOf<AdvertisementWithFilesResponse>()

    fun addAdvertisements(newAdvertisements: List<AdvertisementWithFilesResponse>) {
        advertisementList.addAll(newAdvertisements)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementViewHolder {
        val binding = ItemAdvertisementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdvertisementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdvertisementViewHolder, position: Int) {
        val advertisement = advertisementList[position]
        holder.bind(advertisement)
    }

    override fun getItemCount(): Int = advertisementList.size

    inner class AdvertisementViewHolder(private val binding: ItemAdvertisementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(advertisement: AdvertisementWithFilesResponse) {
            val adResponse = advertisement.advertisementResponse
            binding.textTitle.text = adResponse.title
            binding.textAddDate.text = adResponse.addDate.toString() // Format as needed

            // If the advertisement contains images, load the first one (e.g., with Glide or Picasso)
            if (advertisement.files.isNotEmpty()) {
                // Glide.with(binding.imageAdvertisement.context)
                //     .load(advertisement.files[0]) // Assuming it's a byte array, convert it accordingly
                //     .into(binding.imageAdvertisement)
            }
        }
    }
}