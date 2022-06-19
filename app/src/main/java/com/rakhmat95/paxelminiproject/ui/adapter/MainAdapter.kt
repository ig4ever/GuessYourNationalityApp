package com.rakhmat95.paxelminiproject.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakhmat95.paxelminiproject.R
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.data.model.Prediction
import com.rakhmat95.paxelminiproject.utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MainAdapter(private val data: ArrayList<Country>) : RecyclerView.Adapter<DataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


}

class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_list_search, parent, false)) {
    private var tv_countryName: TextView
    private var tv_probability: TextView
    private var img_country: ImageView

    init {
        tv_countryName = itemView.findViewById(R.id.tv_country_name)
        tv_probability = itemView.findViewById(R.id.tv_probability)
        img_country = itemView.findViewById(R.id.img_country)
    }

    fun bind(country: Country) {
        tv_countryName?.text = country.countryName
        tv_probability?.text = (country.probability * 100).roundToInt().toString() + "%"

        val url_country: String = "https://flagcdn.com/${country.countryId.lowercase(Locale.getDefault())}.svg"
        Utils().fetchSVG(itemView.context, url_country, img_country)

    }
}
