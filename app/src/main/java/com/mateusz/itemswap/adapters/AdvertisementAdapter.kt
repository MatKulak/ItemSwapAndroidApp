package com.mateusz.itemswap.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mateusz.itemswap.R
import com.mateusz.itemswap.activities.AdvertisementActivity
import com.mateusz.itemswap.data.advertisement.AdvertisementWithFileResponse
import com.mateusz.itemswap.data.advertisement.DetailedAdvertisementResponse
import com.mateusz.itemswap.network.APIAdvertisement
import com.mateusz.itemswap.utils.DateUtils.formatDateString
import com.mateusz.itemswap.utils.Utils.decodeBase64ToByteArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdvertisementAdapter(private val advertisements: MutableList<AdvertisementWithFileResponse>,
                           private val apiAdvertisement: APIAdvertisement,
                           private val personalAdvertisement: Boolean = false) :
    RecyclerView.Adapter<AdvertisementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val conditionView: TextView = view.findViewById(R.id.itemCondition)
        val localizationView: TextView = view.findViewById(R.id.itemLocalization)
        val addDateView: TextView = view.findViewById(R.id.itemAddDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_advertisement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advertisement = advertisements[position]
        holder.titleView.text = advertisement.simpleAdvertisementResponse.title
        holder.conditionView.text = advertisement.simpleAdvertisementResponse.condition
        val localizationString = advertisement.simpleAdvertisementResponse.city + ", " +
                advertisement.simpleAdvertisementResponse.street + ", " +
                advertisement.simpleAdvertisementResponse.postalCode;
        holder.localizationView.text = localizationString
        holder.addDateView.text = formatDateString(advertisement.simpleAdvertisementResponse.addDate)

        val decodedFile = decodeBase64ToByteArray(advertisement.file)
        if (decodedFile != null) {
            val bitmap = BitmapFactory.decodeByteArray(decodedFile, 0, decodedFile.size)
            holder.imageView.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener {
            getAdvertisementDetails(advertisement.simpleAdvertisementResponse.id, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int {
        return advertisements.size
    }

    fun addItems(newAdvertisements: List<AdvertisementWithFileResponse>) {
        val startPosition = advertisements.size
        advertisements.addAll(newAdvertisements)
        notifyItemRangeInserted(startPosition, newAdvertisements.size)
    }

    fun clearItems() {
        advertisements.clear()
        notifyDataSetChanged()
    }

    private fun getAdvertisementDetails(id: UUID, context: Context) {
        apiAdvertisement.getAdvertisementById(id).enqueue(object : Callback<DetailedAdvertisementResponse> {
            override fun onResponse(call: Call<DetailedAdvertisementResponse>, response: Response<DetailedAdvertisementResponse>) {
                if (response.isSuccessful) {
                    val detailedAdvertisementResponse = response.body()
                    detailedAdvertisementResponse?.let {
                        val intent = Intent(context, AdvertisementActivity::class.java).apply {
                            putExtra("advertisementDetails", it)
                            putExtra("personalAdvertisement", personalAdvertisement)
                        }
                        context.startActivity(intent)
                    } ?: run {
                        TODO()
                    }
                } else {
                    TODO()
                }
            }

            override fun onFailure(call: Call<DetailedAdvertisementResponse>, t: Throwable) {
                TODO()
            }
        })
    }
}
