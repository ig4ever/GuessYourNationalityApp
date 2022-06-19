package com.rakhmat95.paxelminiproject.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.data.model.Prediction
import com.rakhmat95.paxelminiproject.data.repository.PredictionRepository
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {
    var fetchJob: Job? = null
    var keyword = ""

    var repoPrediction = PredictionRepository()
    var prediction: MutableLiveData<Prediction> = MutableLiveData<Prediction>()
    val _error = MutableLiveData<String>()

    fun fetchDataPrediction(name: String) {
        keyword = name
        fetchJob?.cancel() // cancel previous job when user enters new letter
        fetchJob = viewModelScope.launch {
            delay(500)

            withContext(Dispatchers.IO) {
                try {
                    val result = repoPrediction.getPrediction(name)
                    var listCountryName = URL("https://flagcdn.com/en/codes.json").readText()
                    var jsonCountryName: JSONObject = JSONObject(listCountryName)

                    for (item in result.country) {
                        if (item.countryId.isNotEmpty()) {
                            item.countryName = jsonCountryName.getString(item.countryId.lowercase())
                        }
                    }

                    prediction.postValue(result)
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is IOException -> {
                            _error.postValue("Network Error")
                        }
                        is HttpException -> {
                            val code = throwable.code()
                            val errorResponse = throwable.message()
                            _error.postValue("Error $code $errorResponse")
                        }
                        else -> {
                            _error.postValue("Unknown Error")
                        }
                    }
                }
            }
        }
    }

    fun resetDataPrediction() {
        prediction.value = Prediction("", ArrayList<Country>())
    }

    override fun onCleared() {
        super.onCleared()
    }
}