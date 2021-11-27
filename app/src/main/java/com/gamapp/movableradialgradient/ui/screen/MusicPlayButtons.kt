package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.utils.startMusicService
import com.gamapp.movableradialgradient.viewmodel.MusicPlayerState

@Composable
fun MusicPlayButtons(modifier: Modifier,clickable:Boolean, viewModel: MusicViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val drawables = remember {
        mutableStateListOf(
            ColorState(
                R.drawable.round_shuffle_24,
                R.drawable.outline_shuffle_on_24
            ),
            ColorState(R.drawable.round_fast_rewind_24),
            ColorState(
                R.drawable.round_pause_24,
                R.drawable.round_play_arrow_24,
                onclick = {
                    if (viewModel.musicPlayState.value == MusicPlayerState.NotStarted) {
                        context.startMusicService()
                        viewModel.start()
                    } else if (viewModel.musicPlayState.value == MusicPlayerState.Pause) {
                        viewModel.resume()
                    } else if (viewModel.musicPlayState.value == MusicPlayerState.Started) {
                        viewModel.pause()
                    }
                },
                isSelected = mutableStateOf(false)
            ),
            ColorState(
                R.drawable.round_fast_forward_24,
                onclick = {
                    viewModel.nextMusic()
                }),
            ColorState(
                R.drawable.round_repeat_24,
                R.drawable.round_repeat_one_24,
                onclick = {

                }
            ),
        )
    }

    drawables[2].isSelected.value = viewModel.musicPlayState.value == MusicPlayerState.Started

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        drawables.forEach { item ->
            IconButton(
                enabled = clickable,
                onClick = {
                    item.onclick?.let {
                        it()
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape),
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

}

interface Copyable<out T> where T : Copyable<T> {
    fun getCopy(): T
}

data class ColorState(
    val selected: Int,
    val unSelected: Int? = null,
    var isSelected: MutableState<Boolean> = mutableStateOf(false),
    var onclick: (() -> Unit)? = null
) : Copyable<ColorState> {
    val color get() = unSelected?.let { if (isSelected.value) selected else it } ?: selected
    override fun getCopy(): ColorState = this.copy()
}

fun <E : Copyable<E>> SnapshotStateList<E>.update(item: E, update: (E) -> Unit) {
    val index = indexOf(item)
    if (index != -1) {
        update(item)
        this[index] = item.getCopy()
    }
}