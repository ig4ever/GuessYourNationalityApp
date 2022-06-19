package com.rakhmat95.paxelminiproject.ui.view

import android.graphics.Point
import android.os.Bundle
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
import com.rakhmat95.paxelminiproject.R
import com.rakhmat95.paxelminiproject.data.model.Country
import com.rakhmat95.paxelminiproject.data.model.Prediction
import com.rakhmat95.paxelminiproject.data.viewmodel.MainViewModel
import com.rakhmat95.paxelminiproject.ui.adapter.MainAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), LifecycleOwner {
    lateinit var recylerView: RecyclerView;
    lateinit var mainAdapter: MainAdapter;
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var tv_result_country: TextView
    lateinit var tv_result_name: TextView
    lateinit var tv_label_result: TextView
    lateinit var et_keyword: EditText
    lateinit var tb_switch_view: ToggleButton
    lateinit var empty_view: TextView

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
        et_keyword = findViewById(R.id.et_keyword)
        tb_switch_view =  findViewById(R.id.tb_switch_view)
        empty_view = findViewById(R.id.empty_view)

        mainAdapter = MainAdapter(ArrayList<Country>())
        recylerView.layoutManager = LinearLayoutManager(this)

        tv_label_result.visibility = View.INVISIBLE

        var mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.prediction.observe(this, Observer<Prediction> {
            recylerView.adapter = MainAdapter(it.country)
            tv_result_name.text = it.name

            if (it.name.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                tv_label_result.visibility = View.INVISIBLE
            } else {
                empty_view.visibility = View.INVISIBLE
                tv_label_result.visibility = View.VISIBLE
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
                showError(error)
            }
        })

        et_keyword.doOnTextChanged { text, start, before, count ->
            swipeRefreshLayout.isRefreshing = true
            if (text.toString().isEmpty()) {
                mainViewModel.resetDataPrediction()
            } else if (count > 0) {
                mainViewModel.fetchDataPrediction(text.toString())
            }
        }

        tb_switch_view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recylerView.layoutManager = GridLayoutManager(this, 2)

            } else {
                recylerView.layoutManager = LinearLayoutManager(this)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            mainViewModel.resetDataPrediction()
            mainViewModel.fetchDataPrediction(mainViewModel.keyword)
        }
    }

    private fun showError(error: String?) {
        Toast.makeText(this, "Error $error", Toast.LENGTH_SHORT).show()
    }

    private fun showResult(result: Prediction) {
        Toast.makeText(this, "You search ${result.name}", Toast.LENGTH_SHORT).show()
    }
}