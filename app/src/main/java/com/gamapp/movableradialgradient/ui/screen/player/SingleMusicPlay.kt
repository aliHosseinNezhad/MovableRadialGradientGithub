package com.gamapp.movableradialgradient.ui.screen.player

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import kotlinx.coroutines.launch

const val Expanded = 0
const val Minimal = 1

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

@Composable
fun NavigationBarColor(status: Boolean) {
    val context = LocalContext.current
    val activity = context as Activity
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        activity.window.isStatusBarContrastEnforced = true
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MusicPlayer(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    playViewModel: MusicPlayViewModel = hiltViewModel()
) {
    NavigationBarColor(status = playViewModel.swipeableState.currentValue == Expanded)
    DisposableEffect(key1 = "start") {
        playViewModel.init()
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
            val coroutineScope = rememberCoroutineScope()
            val density = LocalDensity.current.density
            val min = 60 * density
            val max = rect.height * 1f
            val anchors = mapOf(max to Expanded, min to Minimal)
            val swappableState = playViewModel.swipeableState

            val height = (swappableState.offset.value).coerceIn(min, max) / density

            val motionPercent = ((swappableState.offset.value - min) / (max - min)).coerceIn(0f, 1f)
            BackHandler(swappableState.currentValue == Expanded) {
                coroutineScope.launch {
                    swappableState.animateTo(Minimal)
                }
            }
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
                        .height(height.dp),
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
                            clickable = Expanded == swappableState.currentValue
                        )
                        Spacer(modifier = Modifier.padding(bottom = (navigationBarHeight + 50.dp) * motionPercent))
                    }
                    MinimalMusicController(motionPercent, Minimal == swappableState.currentValue)
                }
                Spacer(modifier = Modifier.padding(bottom = navigationBarHeight * (1 - motionPercent)))
            }
        }
    }
}


