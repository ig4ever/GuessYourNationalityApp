package com.rakhmat95.paxelminiproject.data.model


import com.google.gson.annotations.SerializedName

data class Prediction(
    @SerializedName("name")
    var name: String,
    @SerializedName("country")
    val country: ArrayList<Country>
)