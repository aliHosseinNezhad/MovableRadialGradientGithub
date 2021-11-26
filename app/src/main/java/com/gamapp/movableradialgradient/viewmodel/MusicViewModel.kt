package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.movableradialgradient.MediaContainer
import com.gamapp.movableradialgradient.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MusicPlayerState {
    NotStarted,
    Pause,
    Started
}

@HiltViewModel
class MusicViewModel @Inject constructor(
    application: Application,
    private val mediaContainer: MediaContainer,
    private val uiModeManager: UiModeManager
) : AndroidViewModel(application) {

    val musicPlayState = mutableStateOf(MusicPlayerState.NotStarted)
    val seekState: MutableState<Float> = mutableStateOf(0f)
    val isDark = mutableStateOf(false)

    fun onStart() {
        mediaContainer.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                seekState.value = mediaPlayer.currentPosition / mediaPlayer.duration.toFloat()
                musicPlayState.value = MusicPlayerState.Started
            } else if (mediaPlayer.currentPosition != 0) {
                seekState.value = mediaPlayer.currentPosition / mediaPlayer.duration.toFloat()
                musicPlayState.value = MusicPlayerState.Pause
            }
        }
    }

    fun nextMusic() {
        val context = getApplication<Application>()
//        mediaPlayer.setNextMediaPlayer(MediaPlayer.create(context, R.raw.ahay_khabardar))
        Toast.makeText(context, "any thing is ok", Toast.LENGTH_SHORT).show()
    }


    fun setDarkMode(isDark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(
                if (isDark) UiModeManager.MODE_NIGHT_YES
                else UiModeManager.MODE_NIGHT_NO
            )
        }
    }

    private suspend fun updateSeekState(seek: Float? = null) {
        seek?.let { seekTo(it) }
        mediaContainer.mediaPlayer?.let { mediaPlayer ->
            while (mediaPlayer!= null &&mediaPlayer.isPlaying) {
                delay(100)
                seekState.value = mediaPlayer.let {
                    it.currentPosition / it.duration.toFloat()
                }
            }
        }
        if (musicPlayState.value == MusicPlayerState.Started)
            restart()
    }

    private fun restart() {
        mediaContainer.mediaPlayer?.let {
            it.pause()
            seekTo(0f)
            musicPlayState.value = MusicPlayerState.NotStarted
        }
    }

    fun seekTo(seek: Float) {
        mediaContainer.mediaPlayer?.let {
            seekState.value = seek
            it.seekTo((seek * it.duration).toInt())
        }
    }

    fun pause() {
        mediaContainer.mediaPlayer?.let {
            it.pause()
            musicPlayState.value = MusicPlayerState.Pause
        }
    }

    fun resume() {
        mediaContainer.mediaPlayer?.let {
            it.start()
            musicPlayState.value = MusicPlayerState.Started
            viewModelScope.launch(Dispatchers.Default) {
                updateSeekState(seekState.value)
            }
        }
    }

    fun start() {
        val context = getApplication<Application>()
        if (mediaContainer.mediaPlayer == null)
            mediaContainer.mediaPlayer = MediaPlayer.create(context, R.raw.hozier_take_me_to_church)
        mediaContainer.mediaPlayer?.let { mediaPlayer ->
            mediaPlayer.start()
            musicPlayState.value = MusicPlayerState.Started
            viewModelScope.launch(Dispatchers.Default) {
                updateSeekState()
            }
        }
    }
}