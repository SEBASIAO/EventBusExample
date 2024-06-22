package com.example.eventbus2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.eventbus2.eventBus.EventBus
import com.example.eventbus2.eventBus.SportEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SportService : Service() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    fun saveResult(result: SportEvent.ResultSuccess) {
        scope.launch {
            val response =
                if (result.isWarning) {
                    SportEvent.ResultError(
                        30,
                        "Error al guardar",
                    )
                } else {
                    SportEvent.SaveEvent
                }

            EventBus.instance().publish(response)
        }
    }

    fun setupSubscribers(viewScope: CoroutineScope) {
        viewScope.launch {
            EventBus.instance().subscribe<SportEvent> {
                when (it) {
                    is SportEvent.ClosedAdEvent -> {
                        Log.i("SERVICE EVENT BUS", "Ad was closed")
                    }
                    else -> {}
                }
            }
        }
    }

    companion object {
        private val _service: SportService by lazy { SportService() }

        fun instance() = _service
    }
}
