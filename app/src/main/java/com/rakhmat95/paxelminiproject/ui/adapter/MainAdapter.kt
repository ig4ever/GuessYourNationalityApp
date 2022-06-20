package com.rakhmat95.paxelminiproject.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakhmat95.paxelminiproject.R
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.utils.Utils
import java.util.*
import kotlin.math.roundToInt


class MainAdapter(private val data: ArrayList<Country>, private val layoutManager: GridLayoutManager, private val resources: Resources?) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(view: View, val resources: Resources?) : RecyclerView.ViewHolder(view) {
        private var tv_countryName: TextView
        private var tv_probability: TextView
        private var tv_label_probability: TextView
        private var img_country: ImageView

        init {
            tv_countryName = itemView.findViewById(R.id.tv_country_name)
            tv_probability = itemView.findViewById(R.id.tv_probability)
            tv_label_probability = itemView.findViewById(R.id.tv_label_probability)
            img_country = itemView.findViewById(R.id.img_country)
        }

        fun bind(country: Country) {
            tv_countryName?.text = country.countryName
            tv_probability?.text = (country.probability * 100).roundToInt().toString() + "%"
            if (resources != null) {
                tv_label_probability.setText(resources?.getString(R.string.label_probability))
            }

            val url_country: String = "https://flagcdn.com/${country.countryId.lowercase(Locale.getDefault())}.svg"
            Utils().fetchSVG(itemView.context, url_country, img_country)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return layoutManager.spanCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 2) {
            view  = inflater.inflate(R.layout.item_list_search_grid, parent, false)
        } else {
            view = inflater.inflate(R.layout.item_list_search, parent, false)
        }
        return ViewHolder(view, resources!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
