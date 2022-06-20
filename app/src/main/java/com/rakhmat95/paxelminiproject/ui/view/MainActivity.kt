package com.rakhmat95.paxelminiproject.ui.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.internal.ContextUtils
import com.rakhmat95.paxelminiproject.R
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.data.model.Prediction
import com.rakhmat95.paxelminiproject.data.viewmodel.MainViewModel
import com.rakhmat95.paxelminiproject.ui.adapter.MainAdapter
import com.rakhmat95.paxelminiproject.utils.LocaleHelper
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), LifecycleOwner {
    lateinit var recylerView: RecyclerView;
    lateinit var mainAdapter: MainAdapter;
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var tv_result_country: TextView
    lateinit var tv_result_name: TextView
    lateinit var tv_label_result: TextView
    lateinit var title_1: TextView
    lateinit var title_2: TextView
    lateinit var empty_view: TextView

    lateinit var tb_switch_view: ToggleButton
    lateinit var tb_language: ToggleButton

    lateinit var et_keyword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        recylerView = findViewById(R.id.rv_main)
        swipeRefreshLayout = findViewById(R.id.swipe_container)
        tv_result_country = findViewById(R.id.tv_result_country)
        tv_result_name = findViewById(R.id.tv_result_name)
        tv_label_result = findViewById(R.id.tv_label_result)
        title_1 = findViewById(R.id.title_1)
        title_2 = findViewById(R.id.title_2)
        tv_label_result = findViewById(R.id.tv_label_result)
        empty_view = findViewById(R.id.empty_view)
        tb_switch_view =  findViewById(R.id.tb_switch_view)
        tb_language =  findViewById(R.id.tb_language)
        et_keyword = findViewById(R.id.et_keyword)

        mainAdapter = MainAdapter(ArrayList<Country>(), null)
        recylerView.layoutManager = LinearLayoutManager(this)

        tv_label_result.visibility = View.INVISIBLE

        var mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.setLocale(this, "en")

        mainViewModel.prediction.observe(this, Observer<Prediction> {
            recylerView.adapter = MainAdapter(it.country, mainViewModel.resources!!)
            tv_result_name.text = it.name

            if (it.country.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                tv_label_result.visibility = View.INVISIBLE
                tv_result_name.visibility = View.INVISIBLE
            } else {
                empty_view.visibility = View.INVISIBLE
                tv_label_result.visibility = View.VISIBLE
                tv_result_name.visibility = View.VISIBLE
            }

            var highestProbability = Country( "",0.0, "" )

            for (item in it.country) {
                if (item.probability > highestProbability.probability) {
                    highestProbability = item
                }
            }

            tv_result_country.text = highestProbability.countryName
            swipeRefreshLayout.isRefreshing = false
        })

        mainViewModel._error.observe(this, Observer { error ->
            if (error.isNotEmpty()) {
                swipeRefreshLayout.isRefreshing = false
                showError(error)
            }
        })

        et_keyword.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()) {
                swipeRefreshLayout.isRefreshing = true
                mainViewModel.fetchDataPrediction(text.toString())
            } else {
                swipeRefreshLayout.isRefreshing = false
                mainViewModel.resetDataPrediction()
            }
        }

        tb_switch_view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recylerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
            } else {
                recylerView.layoutManager = LinearLayoutManager(this)
            }
        }

        tb_language.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainViewModel.setLocale(this, "id")
            } else {
                mainViewModel.setLocale(this, "en")
            }

            title_1.setText(mainViewModel.resources?.getString(R.string.title_1))
            title_2.setText(mainViewModel.resources?.getString(R.string.title_2))
            tv_label_result.setText(mainViewModel.resources?.getString(R.string.label_result))
            et_keyword.setHint(mainViewModel.resources?.getString(R.string.placeholder_search))
            empty_view.setText(mainViewModel.resources?.getString(R.string.empty_state_info))
            if (mainViewModel.keyword.isNotEmpty()) {
                mainViewModel.fetchDataPrediction(mainViewModel.keyword)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (mainViewModel.keyword !== "") {
                swipeRefreshLayout.isRefreshing = true
                mainViewModel.fetchDataPrediction(mainViewModel.keyword)
            } else {
                swipeRefreshLayout.isRefreshing = false
                mainViewModel.resetDataPrediction()
            }
        }
    }

    private fun showError(error: String?) {
        Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
    }

    private fun showResult(result: Prediction) {
        Toast.makeText(this, "You search ${result.name}", Toast.LENGTH_SHORT).show()
    }
}