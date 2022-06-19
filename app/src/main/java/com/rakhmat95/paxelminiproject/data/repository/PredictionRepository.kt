package com.rakhmat95.paxelminiproject.data.repository

import com.rakhmat95.paxelminiproject.data.ApiPredictionService
import com.rakhmat95.paxelminiproject.data.apiPredictionService

class PredictionRepository {
    private var services: ApiPredictionService = apiPredictionService

    suspend fun getPrediction(name: String) = services.getPrediction(name)
}