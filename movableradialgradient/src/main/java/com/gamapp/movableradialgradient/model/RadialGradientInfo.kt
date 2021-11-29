package com.gamapp.movableradialgradient.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Color

internal data class RadialGradientMotionInfo(
    var color:Color,
    var paint: AndroidPaint,
    var radius: Float,
    var radiusDomain: RadiusDomain,
    var motionPath: (Float) -> Float,
    var speed: Float,
    var center: Offset,
    var coordinate: Offset?,
    var motionRadiusPercent:Float,
    var count:Float = 0f,
)

data class RadialGradientInfo(
    val color: Color,
    val radiusPercent: Float,
    val radiusDomain: RadiusDomain = RadiusDomain(0f, 2f),
    val polarMotionPath: (Float) -> Float,
    val center: Coordinate,
    val speed: Float,
    val motionFieldSizePercent:Float,
)