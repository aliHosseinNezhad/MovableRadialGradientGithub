package com.gamapp.movableradialgradient.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import com.gamapp.movableradialgradient.viewmodel.MusicViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.ui.theme.primary
import java.util.*


@Composable
fun Space(dp: Dp, percent: Float) {
    Spacer(modifier = Modifier.padding(dp / 2f * percent))
}

@Composable
fun RowScope.SpaceWeight(weight: Float, percent: Float) {
    if (weight * percent > 0f)
        Spacer(modifier = Modifier.weight(weight * percent))
}

@Composable
fun ColumnScope.SpaceWeight(weight: Float, percent: Float) {
    Spacer(modifier = Modifier.weight(weight * percent))
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MusicPlayer(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    viewModel: MusicViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = "start") {
        viewModel.onStart()
        onDispose { }
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
            val min = 60 * density
            val max = size.height * 1f
            val anchors = mapOf(max to 0, min to 1)
            val swappableState = rememberSwipeableState(initialValue = 0)
            val motionPercent = ((swappableState.offset.value - min) / (max - min)).coerceIn(0f, 1f)
            Column(Modifier.align(BottomCenter)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp * (1 - motionPercent))
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
                    border = BorderStroke(1.dp, color = Color.Blue)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        BackgroundGradient(
                            Modifier
                                .fillMaxSize(),
                            enable = musicState == MusicPlayerState.Started && motionPercent == 1f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(primary.alpha((1 - motionPercent) * 0.6f))
                        )
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Space(dp = statusBarHeight, percent = motionPercent)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            val percent =
                                (0.5f * motionPercent + (1 - motionPercent) * 0f).coerceIn(0f, 0.5f)
                            val widthPercent =
                                ((0.5f * motionPercent) + (1 - motionPercent) * 0.1f).coerceIn(
                                    0.1f,
                                    0.5f
                                )
                            val startPercent = percent / 2f
                            val endPercent = (1 - percent) / 2f
                            val cornerRadiusPercent =
                                (motionPercent) * 30f + (1 - motionPercent) * 100f
                            SpaceWeight(weight = startPercent, percent = 1f)
                            Image(
                                painter = painterResource(id = R.drawable.round_music_note_24),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .weight(widthPercent)
                                    .clip(RoundedCornerShape(cornerRadiusPercent))
                                    .background(Color.DarkGray.alpha(0.5f))
                                    .aspectRatio(1f, true)
                                    .align(CenterVertically),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            SpaceWeight(weight = endPercent, percent = 1f)
                        }
                        val controllerHeight = (motionPercent - 0.8f).coerceIn(0f, 0.2f) / 0.2f
                        if (motionPercent != 0f)
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    alpha = controllerHeight
                                    this.translationY = -200f * (1 - controllerHeight)
                                }
                                .weight(1f * motionPercent)) {

                            }
                        MusicControllers(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(136.dp * motionPercent)
                                .graphicsLayer {
                                    alpha = controllerHeight
                                    this.translationY = -200f * (1 - controllerHeight)
                                },
                            clickable = motionPercent == 1f
                        )
                        Spacer(modifier = Modifier.padding(bottom = (navigationBarHeight + 16.dp) * motionPercent))
                    }

                }
                Spacer(modifier = Modifier.padding(bottom = navigationBarHeight * (1 - motionPercent)))
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