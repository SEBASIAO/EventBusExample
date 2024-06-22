package com.example.eventbus2.dataAccess

import com.example.eventbus2.eventBus.SportEvent
import kotlin.random.Random

fun getResultEventsInRealtime() = listOf(
    SportEvent.ResultSuccess(1, "Fútbol", listOf("Italia", "Perú", "Corea del Sur")),
    SportEvent.ResultSuccess(
        2,
        "Levantamiento de Pesas",
        listOf("Mongolia", "Alemania", "Turquía")
    ),
    SportEvent.ResultError(10, "Error de red."),
    SportEvent.ResultSuccess(3, "Gimnasia Rítmica", listOf("Rusia", "USA", "Francia")),
    SportEvent.ResultSuccess(4, "Polo Acuático", listOf("España", "Vietnam", "USA")),
    SportEvent.ResultSuccess(5, "Béisbol", null, true),
    SportEvent.ResultError(20, "Error de permisos."),
    SportEvent.ResultSuccess(6, "Rugby", listOf("Sudáfrica", "Qatar", "Rumanía")),
    SportEvent.ResultSuccess(7, "Tenis", listOf("España", "México", "Colombia"))
)

fun getAdEventsInRealtime() = listOf(
    SportEvent.AdEvent,
    SportEvent.AdEvent
)

fun someTime(): Long = Random.nextLong(500, 2000)