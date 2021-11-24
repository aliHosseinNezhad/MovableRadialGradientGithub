package com.gamapp.movableradialgradient.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

internal data class RadialGradientMotionInfo(
    val color: Color,
    val radius: Float,
    val radiusDomain: RadiusDomain,
    val motionPath: (Float) -> Float,
    val speed: Float,
    val center: Coordinate,
    var coordinate: Offset?,
    val motionRadiusPercent:Float,
)

data class RadialGradientInfo(
    val color: Color,
    val radiusPercent: Float,
    val radiusDomain: RadiusDomain = RadiusDomain(0f, 2f),
    val motionPath: (Float) -> Float,
    val center: Coordinate,
    val speed: Float,
    val motionRadiusPercent:Float,
)