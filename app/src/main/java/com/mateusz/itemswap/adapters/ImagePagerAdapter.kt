package com.mateusz.itemswap.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.utils.Utils.decodeBase64ToByteArray

class ImagePagerAdapter(private val base64Images: List<String>) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_pager, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val base64String = base64Images[position]
        val decodedByteArray = decodeBase64ToByteArray(base64String)
        decodedByteArray?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.imageView.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int = base64Images.size

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}