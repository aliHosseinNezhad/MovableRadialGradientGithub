package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.utils.startMusicService
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import kotlinx.coroutines.launch

@Composable
fun MusicControllers(
    modifier: Modifier,
    clickable: Boolean = true,
    playViewModel: MusicPlayViewModel = hiltViewModel()
) {
    Column(modifier = modifier) {
        SeekBar(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(60.dp),
            clickable
        )
        Spacer(modifier = Modifier.padding(8.dp))
        MusicPlayButtons(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            clickable
        )
    }

}


@ExperimentalMaterialApi
@Composable
fun MinimalMusicController(
    motionPercent: Float,
    clickable: Boolean,
    playViewModel: MusicPlayViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val buttons = remember {
        mutableStateListOf(
            ButtonModel(
                selected = R.drawable.round_fast_rewind_24,
            ),
            ButtonModel(
                selected = R.drawable.round_play_arrow_24,
                unSelected = R.drawable.round_pause_24,
                onclick = {
                    if (playViewModel.musicPlayState.value == MusicPlayerState.NotStarted) {
                        playViewModel.start()
                    } else if (playViewModel.musicPlayState.value == MusicPlayerState.Pause) {
                        playViewModel.resume()
                    } else if (playViewModel.musicPlayState.value == MusicPlayerState.Started) {
                        playViewModel.pause()
                    }
                }
            ),
            ButtonModel(
                selected = R.drawable.round_fast_forward_24,
            ),
            ButtonModel(
                selected = R.drawable.round_playlist_add_24
            )
        )
    }
    val interactionSource = remember { MutableInteractionSource() }
    buttons[1].isSelected.value = (playViewModel.musicPlayState.value != MusicPlayerState.Started)
    val controllerHeight = (1 - motionPercent - 0.8f).coerceIn(0f, 0.2f) / 0.2f
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .composed {
                    if (clickable)
                        this.clickable(interactionSource = interactionSource, indication = null) {
                            coroutineScope.launch {
                                playViewModel.swipeableState.animateTo(0)
                            }
                        }
                    else this
                }
                .fillMaxWidth(0.85f)
                .align(Alignment.BottomEnd)
                .height(60.dp)
                .graphicsLayer {
                    alpha = controllerHeight
                }
        ) {
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Music Name hhhhhhhh",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.padding(bottom = 4.dp))
                Text(
                    text = "Music Details hhhh",
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            buttons.forEach { item ->
                IconButton(
                    enabled = clickable,
                    onClick = {
                        item.onclick?.let {
                            it()
                        }
                    },
                    modifier = Modifier
//                        .size(40.dp)
                        .weight(0.5f)
                        .fillMaxHeight()
                        .align(CenterVertically)
                        .clip(CircleShape),
                ) {
                    Icon(
                        painter = painterResource(id = item.color),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.padding(start = 16.dp))
        }
    }
}

