package com.example.pasturecontrol

data class PastureDisplay(
    val pasture: Pasture,
    val state: RotationState,
    val redStartMillis: Long? = null,
    val redEndMillis: Long? = null,
    val orangeStartMillis: Long? = null,
    val orangeEndMillis: Long? = null
)

enum class RotationState(val label: String) {
    Occupied("Rojo - con ganado"),
    Recovering("Anaranjado - en recuperacion"),
    Available("Verde - disponible")
}
