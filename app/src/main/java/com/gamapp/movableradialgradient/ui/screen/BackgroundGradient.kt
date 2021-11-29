package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.MotionRadialGradient
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.model.Coordinate
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import com.gamapp.movableradialgradient.ui.theme.primary
import com.gamapp.movableradialgradient.viewmodel.MusicListViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BackgroundGradient(
    modifier: Modifier,
    rect: Rect,
    enable: Boolean,
    viewModel: MusicPlayViewModel = hiltViewModel()
) {
    val colors by viewModel.colors
    MotionRadialGradient(
        modifier = modifier,
        items = listOf(
            RadialGradientInfo(
                color = colors[0],
                radiusPercent = 3f,
                speed = 2f,
                polarMotionPath = {
                    sin(2 * it)
                },
                center = Coordinate(0.5f, 0.5f),
                motionFieldSizePercent = 1.5f
            ),
            RadialGradientInfo(
                color = colors[1],
                radiusPercent = 1.6f,
                speed = 2f,
                polarMotionPath = {
                    sin(2 * (it + PI.toFloat() / 4))
                },
                center = Coordinate(0.5f, 0.5f),
                motionFieldSizePercent = 1.5f
            )
        ),
        enable = enable,
        rect = rect
    )
}


@Composable
fun BackColor(rect: Rect, viewModel: MusicPlayViewModel = hiltViewModel(), motionPercent: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGradient(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            rect,
            enable = viewModel.musicPlayState.value == MusicPlayerState.Started && motionPercent == 1f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(primary.alpha((1 - motionPercent) * 0.6f))
        )
    }
}