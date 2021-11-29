package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import kotlin.math.PI
import kotlin.math.sin

@ExperimentalMaterialApi
@Composable
fun ColumnScope.MusicImage(
    motionPercent: Float,
    viewModel: MusicPlayViewModel = hiltViewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        val percent =
            (0.5f * motionPercent + (1 - motionPercent) * 0f).coerceIn(0f, 0.5f)
        val maxWidthPercent by animateFloatAsState(
            targetValue = if (
                viewModel.swipeableState.currentValue == Expanded &&
                viewModel.musicPlayState.value == MusicPlayerState.Started
            ) {
                0.8f
            } else 0.5f,
            animationSpec = tween(durationMillis = 200, easing = {
                sin(it * PI / 2f).toFloat()
            })
        )
        val widthPercent =
            ((maxWidthPercent * motionPercent) + (1 - motionPercent) * 0.1f).coerceIn(
                0.1f,
                maxWidthPercent
            )
        val startPercent = percent / 2f
        val endPercent = (1 - percent) / 2f
        val cornerRadiusPercent =
            (motionPercent) * 60f + (1 - motionPercent) * 100f
        SpaceWeight(weight = startPercent)
        val bitmap = viewModel.musicModel.bitmap.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(widthPercent)
                    .clip(RoundedCornerShape(cornerRadiusPercent))
                    .background(Color.DarkGray.alpha(0.5f))
                    .aspectRatio(1f, true)
                    .align(Alignment.CenterVertically),
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.round_music_note_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(widthPercent)
                    .clip(RoundedCornerShape(cornerRadiusPercent))
                    .background(Color.DarkGray.alpha(0.5f))
                    .aspectRatio(1f, true)
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        SpaceWeight(weight = endPercent)
    }
}

@Composable
fun ColumnScope.MusicTitles(
    motionPercent: Float,
    viewModel: MusicPlayViewModel = hiltViewModel()
) {
    val controllerHeight = (motionPercent - 0.8f).coerceIn(0f, 0.2f) / 0.2f
    if (motionPercent != 0f)
        Column(modifier = Modifier
            .fillMaxWidth(0.8f)
            .align(Alignment.CenterHorizontally)
            .graphicsLayer {
                alpha = controllerHeight
            }
            .weight(1f * motionPercent),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = viewModel.musicModel.name.value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Space(dp = 8.dp, percent = 1f)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = viewModel.musicModel.details.value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
}

