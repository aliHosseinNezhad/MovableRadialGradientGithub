package com.gamapp.movableradialgradient.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gamapp.movableradialgradient.MotionRadialGradient
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.model.Coordinate
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BackgroundGradient(modifier: Modifier, enable: Boolean) {
    MotionRadialGradient(
        modifier = modifier,
        items = listOf(
            RadialGradientInfo(
                color = Color(0, 100, 255).alpha(0.9f),
                radiusPercent = 3f,
                speed = 2f,
                polarMotionPath = {
                    sin(2 * it)
                },
                center = Coordinate(0.5f, 0.5f),
                motionFieldSizePercent = 1.8f
            ),
            RadialGradientInfo(
                color = Color.Magenta.alpha(0.5f),
                radiusPercent = 1.6f,
                speed = 2f,
                polarMotionPath = {
                    sin(2 * (it + PI.toFloat() / 4))
                },
                center = Coordinate(0.5f, 0.5f),
                motionFieldSizePercent = 1.5f
            )
        ),
        enable = enable
    )
}