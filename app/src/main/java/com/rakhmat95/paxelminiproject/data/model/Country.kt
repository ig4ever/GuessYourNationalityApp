package com.rakhmat95.paxelminiproject.data.model


import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("probability")
    val probability: Double,
    var countryName: String
    )