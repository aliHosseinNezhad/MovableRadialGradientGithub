package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults.AnimationSpec
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.movableradialgradient.MediaContainer
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.ui.screen.LoadImage
import com.gamapp.movableradialgradient.utils.startMusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

enum class MusicPlayerState {
    NotStarted,
    Pause,
    Started
}

data class MusicModel(
    var name: MutableState<String> = mutableStateOf(""),
    var details: MutableState<String> = mutableStateOf(""),
    var id: MutableState<Long?> = mutableStateOf(null),
    var bitmap: MutableState<ImageBitmap?> = mutableStateOf(null)
)

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val application: Application,
    private val mediaContainer: MediaContainer,
    private val uiModeManager: UiModeManager
) : ViewModel() {

    @ExperimentalMaterialApi
    val swipeableState = SwipeableState(
        initialValue = 1,
        animationSpec = AnimationSpec,
        confirmStateChange = { true }
    )
    val musicModel: MusicModel = MusicModel()

    val musicPlayState = mutableStateOf(MusicPlayerState.NotStarted)
    val seekState: MutableState<Float> = mutableStateOf(0f)
    private val context: Context get() = application.applicationContext

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
            while (mediaPlayer != null && mediaPlayer.isPlaying) {
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
        mediaContainer.mediaPlayer?.let { mediaPlayer ->
            context.startMusicService()
            mediaPlayer.start()
            musicPlayState.value = MusicPlayerState.Started
            viewModelScope.launch(Dispatchers.Default) {
                updateSeekState()
            }
        }
    }

    fun setMusic(item: Audio) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaContainer.mediaPlayer?.reset()
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                item.id
            )
            mediaContainer.mediaPlayer = MediaPlayer.create(context, uri)
            musicModel.id.value = item.id
            musicModel.name.value = item.name
            musicModel.details.value = "details"
            musicModel.bitmap.value = getBitmap(item.id)
            onStart()
            start()
        }

    }

    fun getBitmap(id: Long): ImageBitmap? {
        return try {
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(
                context, ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
            )
            val data = mmr.embeddedPicture
            data?.let { bytes ->
                val bitmap =
                    BitmapFactory.decodeByteArray(
                        bytes,
                        0,
                        bytes.size
                    )
                mmr.release()
                bitmap.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}