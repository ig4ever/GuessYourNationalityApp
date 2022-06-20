package com.rakhmat95.paxelminiproject.data.viewmodel

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.data.model.Prediction
import com.rakhmat95.paxelminiproject.data.repository.PredictionRepository
import com.rakhmat95.paxelminiproject.utils.LocaleHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {
    var fetchJob: Job? = null
    var resources: Resources? = null
    var keyword = ""

    var repoPrediction = PredictionRepository()
    var prediction: MutableLiveData<Prediction> = MutableLiveData<Prediction>()
    val _error = MutableLiveData<String>()

    fun fetchDataPrediction(name: String) {
        keyword = name.replace(" ", "%20")
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            delay(500)
            withContext(Dispatchers.IO) {
                try {
                    if (keyword !== "") {
                        val result = repoPrediction.getPrediction(keyword)
                        var listCountryName = URL("https://flagcdn.com/en/codes.json").readText()
                        var jsonCountryName: JSONObject = JSONObject(listCountryName)

                        for (item in result.country) {
                            if (item.countryId.isNotEmpty()) {
                                item.countryName = jsonCountryName.getString(item.countryId.lowercase())
                            }
                        }
                        prediction.postValue(result)
                    } else {
                        prediction.postValue(Prediction("", ArrayList<Country>()))
                    }
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
                    }
                }
            }
        }
    }

    fun resetDataPrediction() {
        keyword = ""
        prediction.value = Prediction("", ArrayList<Country>())
    }

    fun setLocale(context: Context, countryId: String) {
        val context = LocaleHelper.setLocale(context, countryId)
        resources = context.getResources()
    }

    fun sort() {
        prediction.value?.country?.reverse()
        val reversedListCountry = prediction.value?.country
        prediction.value = Prediction(prediction.value?.name!!, reversedListCountry!!)
    }

    override fun onCleared() {
        super.onCleared()
    }
}