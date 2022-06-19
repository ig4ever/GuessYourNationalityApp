package com.rakhmat95.paxelminiproject.data

import com.rakhmat95.paxelminiproject.data.model.Prediction
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiPredictionService {
    @GET("/")
    suspend fun getPrediction(@Query("name") name: String): Prediction
}

val apiPredictionService: ApiPredictionService by lazy {
    Retrofit.Builder()
        .baseUrl("https://api.nationalize.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiPredictionService::class.java)
}