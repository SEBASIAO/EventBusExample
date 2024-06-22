package com.example.eventbus2.adapter

import com.example.eventbus2.eventBus.SportEvent

interface OnClickListener {
    fun onClick(result: SportEvent.ResultSuccess)
}