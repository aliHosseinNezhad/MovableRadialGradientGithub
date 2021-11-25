package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.gamapp.movableradialgradient.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MusicPlayerState {
    class NotStarted : MusicPlayerState()
    class Pause : MusicPlayerState()
    class Started : MusicPlayerState()
}

@HiltViewModel
class MusicViewModel @Inject constructor(
    val mediaPlayer: MediaPlayer?
) : ViewModel() {

    fun seekTo(seek: Float) {

    }

    fun pause() {

    }

    fun resume() {

    }

    fun start() {

    }

    val musicPlayState = mutableStateOf(MusicPlayerState.NotStarted)
    val seekState: MutableState<Float> = mutableStateOf(0f)
    val isDark = mutableStateOf(false)
}