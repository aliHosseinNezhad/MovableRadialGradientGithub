package com.gamapp.movableradialgradient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gamapp.movableradialgradient.model.Coordinate
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import kotlin.math.PI
import kotlin.math.sin
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.gamapp.movableradialgradient.ui.theme.MovableRadialGradientTheme
import com.gamapp.movableradialgradient.utils.*


interface Copyable<out T> where T : Copyable<T> {
    fun getCopy(): T
}

data class ColorState(
    val selected: Int,
    val unSelected: Int? = null,
    var isSelected: Boolean = true,
) : Copyable<ColorState> {
    val color get() = unSelected?.let { if (isSelected) selected else it } ?: selected
    override fun getCopy(): ColorState = this.copy()
}

fun <E : Copyable<E>> SnapshotStateList<E>.update(item: E, update: (E) -> Unit) {
    val index = indexOf(item)
    if (index != -1) {
        update(item)
        this[index] = item.getCopy()
    }
}


class MainActivity : ComponentActivity() {
    var statusBarHeight: Dp = 0.dp
    var navigationBarHeight: Dp = 0.dp

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarHeight = statusBarHeight(this)
        navigationBarHeight = navigateBarHeight(this)
        Log.i("navigationBarHeight", "$navigationBarHeight")
        setContent {
            var isDark by remember {
                mutableStateOf(false)
            }
            var heightPercent = animateFloatAsState(
                targetValue = 0.2f
            )
            SetSystemColorByAnimation(
                isDarkMode = isDark,
                dark = Color.Black,
                light = Color.White
            )
            var size by remember {
                mutableStateOf<IntSize?>(null)
            }
            MovableRadialGradientTheme(darkTheme = isDark) {
                Box(
                    modifier = Modifier
                        .onSizeChanged { size = it }
                        .fillMaxSize()
                        .background(if (isDark) Color.Black else Color.White)
                        .padding(top = statusBarHeight, bottom = navigationBarHeight)
                ) {
                    size?.let { size ->
                        val anchors = mapOf(size.height * 1f to 0, size.height / 2f to 1)
                        val density = LocalDensity.current.density
                        val swappableState = rememberSwipeableState(initialValue = 0)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(BottomCenter)
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
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, color = Color.Blue)
                        ) {
                            if (size.height > 0f)
                                BackgroundGradient(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(20.dp)
                                )
                            Column(modifier = Modifier.fillMaxSize()) {
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.round_music_note_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .aspectRatio(1f)
                                        .align(CenterHorizontally),
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {

                                    val drawables = remember {
                                        mutableStateListOf(
                                            ColorState(
                                                R.drawable.round_shuffle_24,
                                                R.drawable.outline_shuffle_on_24
                                            ),
                                            ColorState(R.drawable.round_fast_rewind_24),
                                            ColorState(
                                                R.drawable.round_play_arrow_24,
                                                R.drawable.round_pause_24
                                            ),
                                            ColorState(R.drawable.round_fast_forward_24),
                                            ColorState(
                                                R.drawable.round_repeat_24,
                                                R.drawable.round_repeat_one_24
                                            ),
                                        )
                                    }
                                    drawables.forEach { item ->
                                        IconButton(
                                            onClick = {
                                                drawables.update(item) {
                                                    it.isSelected = !it.isSelected
                                                }
                                            }, modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(CircleShape),
                                            enabled = true
                                        ) {
                                            Icon(
                                                painter = painterResource(id = item.color),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(19.dp)
                                                    .fillMaxSize(),
                                                tint = Color.White
                                            )
                                        }
                                    }


                                }
                                Spacer(modifier = Modifier.padding(bottom = 32.dp))
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            isDark = !isDark
                            changeDarkMode(isDark)

                        }, modifier = Modifier
                            .padding(32.dp)
                            .size(30.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isDark) R.drawable.round_dark_mode_24
                                else R.drawable.round_light_mode_24
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BackgroundGradient(modifier: Modifier) {
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
        )
    )
}
