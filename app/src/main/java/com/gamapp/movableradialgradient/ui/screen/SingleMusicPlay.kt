package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.utils.StatusBarColor
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import com.gamapp.movableradialgradient.viewmodel.MusicViewModel
import com.gamapp.movableradialgradient.R
@ExperimentalMaterialApi
@Composable
fun MusicPlayer(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    viewModel: MusicViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = "start"){
        viewModel.onStart()
        onDispose {  }
    }
    val isDark = true
    val musicState by viewModel.musicPlayState
    var size by remember {
        mutableStateOf<IntSize?>(null)
    }
    Box(
        modifier = Modifier
            .onSizeChanged { size = it }
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
    ) {
        size?.let { size ->
            val density = LocalDensity.current.density
            val end = 80 * density
            val start = size.height * 1f
            val anchors = mapOf(start to 0, end to 1)
            val swappableState = rememberSwipeableState(initialValue = 0)
            val borderPercent = 1f / (start - end) * (swappableState.offset.value - end)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .swipeable(
                        state = swappableState,
                        anchors = anchors,
                        orientation = Orientation.Vertical,
                        thresholds = { _, _ ->
                            FractionalThreshold(0.3f)
                        },
                        reverseDirection = true,
                        velocityThreshold = 8.dp
                    )
                    .height((swappableState.offset.value / density).dp),
                shape = RoundedCornerShape(
                    (50 * (1f - borderPercent)).coerceIn(
                        0f,
                        50f
                    )
                ),
                border = BorderStroke(1.dp, color = Color.Blue)
            ) {
                BackgroundGradient(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    enable = musicState == MusicPlayerState.Started
                )
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.padding(bottom = statusBarHeight + 16.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.round_music_note_24),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                            .align(Alignment.CenterHorizontally),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    MusicControllers(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.padding(bottom = navigationBarHeight + 16.dp))
                }
            }
        }
        IconButton(
            onClick = {
                viewModel.setDarkMode(!isDark)
            }, modifier = Modifier
                .padding(top = statusBarHeight + 16.dp)
                .padding(horizontal = 32.dp)
                .size(30.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(
                    id = if (!isDark) R.drawable.round_dark_mode_24
                    else R.drawable.round_light_mode_24
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )
        }
    }
}