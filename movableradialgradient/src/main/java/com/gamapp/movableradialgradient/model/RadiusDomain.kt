package com.gamapp.movableradialgradient.model

import kotlin.math.PI

data class RadiusDomain(val start: Float, val end: Float) {
    fun getDifference() = end * PI.toFloat() - start * PI.toFloat()
}