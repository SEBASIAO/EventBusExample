package com.example.eventbus2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventbus2.adapter.OnClickListener
import com.example.eventbus2.adapter.ResultAdapter
import com.example.eventbus2.dataAccess.getAdEventsInRealtime
import com.example.eventbus2.dataAccess.getResultEventsInRealtime
import com.example.eventbus2.dataAccess.someTime
import com.example.eventbus2.databinding.ActivityMainBinding
import com.example.eventbus2.eventBus.EventBus
import com.example.eventbus2.eventBus.SportEvent
import com.example.eventbus2.service.SportService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity :
    AppCompatActivity(),
    OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
        setupSubscribers()
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    val events = getAdEventsInRealtime()
                    EventBus.instance().publish(events.first())
                }
            }
            setOnLongClickListener {
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    EventBus.instance().publish(SportEvent.ClosedAdEvent)
                }
                true
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            adapter.clear()
            getEvents()
            binding.btnAd.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        binding.srlResults.isRefreshing = true
        getEvents()
    }

    private fun setupSubscribers() {
        lifecycleScope.launch {
            SportService.instance().setupSubscribers(this)
            EventBus.instance().subscribe<SportEvent> {
                binding.srlResults.isRefreshing = false
                when (it) {
                    is SportEvent.ResultSuccess -> {
                        adapter.add(it)
                    }

                    is SportEvent.ResultError -> {
                        Snackbar
                            .make(
                                binding.root,
                                "ERROR: ${it.code} - ${it.message}",
                                Snackbar.LENGTH_LONG,
                            ).show()
                    }

                    is SportEvent.AdEvent -> {
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Ad Click. Send data to server...",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }

                    is SportEvent.ClosedAdEvent -> {
                        binding.btnAd.visibility = View.GONE
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Ad Closed. Send data to server...",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }

                    is SportEvent.SaveEvent -> {
                        Toast.makeText(this@MainActivity, "Guardado", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getEvents() {
        lifecycleScope.launch {
            val events = getResultEventsInRealtime()
            events.forEach {
                delay(someTime())
                EventBus.instance().publish(it)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)
    }

    // OnClickListener
    override fun onClick(result: SportEvent.ResultSuccess) {
        binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            // EventBus.instance().publish(SportEvent.SaveEvent)
            SportService.instance().saveResult(result)
        }
    }
}
