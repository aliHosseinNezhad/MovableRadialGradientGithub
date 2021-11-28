package com.gamapp.movableradialgradient.ui.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.ui.theme.primary


@Composable
fun Space(dp: Dp, percent: Float) {
    Spacer(modifier = Modifier.padding(dp / 2f * percent))
}

@Composable
fun RowScope.SpaceWeight(weight: Float) {
    if (weight > 0f)
        Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.SpaceWeight(weight: Float) {
    if (weight > 0f)
        Spacer(modifier = Modifier.weight(weight))
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MusicPlayer(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    playViewModel: MusicPlayViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = "start") {
        playViewModel.onStart()
        onDispose { }
    }
    var rect by remember {
        mutableStateOf(null as Rect?)
    }
    Box(
        modifier = Modifier
            .onGloballyPositioned {
                rect = it.boundsInParent()
            }
            .fillMaxSize()
    ) {
        rect?.let { rect ->
            val density = LocalDensity.current.density
            val min = 60 * density
            val max = rect.height * 1f
            val anchors = mapOf(max to 0, min to 1)
            val swappableState = playViewModel.swipeableState
            val motionPercent = ((swappableState.offset.value - min) / (max - min)).coerceIn(0f, 1f)
            Column(Modifier.align(BottomCenter)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp * (1 - motionPercent))
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
                        (100 * (1f - motionPercent)).coerceIn(
                            0f,
                            100f
                        )
                    ),
                ) {
                    BackColor(rect = rect, motionPercent = motionPercent)
                    Column(modifier = Modifier.fillMaxSize()) {
                        Space(dp = statusBarHeight, percent = motionPercent)
                        val controllerHeight = (motionPercent - 0.8f).coerceIn(0f, 0.2f) / 0.2f
                        MusicImage(motionPercent = motionPercent)
                        MusicTitles(motionPercent = motionPercent)
                        MusicControllers(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(136.dp * motionPercent)
                                .graphicsLayer {
                                    alpha = controllerHeight
                                },
                            clickable = 0 == swappableState.currentValue
                        )
                        Spacer(modifier = Modifier.padding(bottom = (navigationBarHeight + 50.dp) * motionPercent))
                    }
                    MinimalMusicController(motionPercent, 1 == swappableState.currentValue)
                }
                Spacer(modifier = Modifier.padding(bottom = navigationBarHeight * (1 - motionPercent)))
            }
        }
    }
}


