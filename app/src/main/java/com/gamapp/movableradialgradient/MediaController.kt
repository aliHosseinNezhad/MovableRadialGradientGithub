package com.gamapp.movableradialgradient

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import com.gamapp.movableradialgradient.entity.AudioEntity
import com.gamapp.movableradialgradient.repository.MusicRepository
import com.gamapp.movableradialgradient.service.MusicStateChange
import com.gamapp.movableradialgradient.utils.startMusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class MusicPlayerState {
    NotStarted,
    Pause,
    Started
}

@Singleton
class MediaController @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val musicRepository: MusicRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var musicStateChange: MusicStateChange? = null
    val playerState = mutableStateOf(MusicPlayerState.NotStarted)
    val seekState = mutableStateOf(0f)


    fun setOnStateChangeListener(state: MusicStateChange) {
        musicStateChange = state
    }

    var currentPlayerList: List<AudioEntity> = musicRepository.getAllMusicFromFile()
    var mediaPlayer: MediaPlayer? = null
    var audioEntity: AudioEntity? = null

    fun set(item: AudioEntity) {
        mediaPlayer?.reset()
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            item.id
        )
        mediaPlayer = MediaPlayer.create(context, uri)
        audioEntity = item
    }

    private fun seekObserver() {
        mediaPlayer?.let {
            coroutineScope.launch {
                while (it.isPlaying) {
                    delay(100)
                    val seek = it.currentPosition / it.duration.toFloat()
                    onSeekChange(seek)
                }
                onMusicEnd()
            }
        }
    }

    fun seekTo(seek: Float) {
        mediaPlayer?.let {
            seekState.value = seek
            it.seekTo((seek * it.duration).toInt())
        }
    }

    fun repeat() {
        mediaPlayer?.let {
            seekTo(0f)
            play()
        }
    }

    fun play() {
        mediaPlayer?.let {
            context.startMusicService()
            it.start()
            onStart()
            seekObserver()
        }
    }

    fun resume() {
        mediaPlayer?.let {
            context.startMusicService()
            it.seekTo((seekState.value * it.duration).toInt())
            it.start()
            onStart()
            seekObserver()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            it.pause()
            onPause()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            pause()
            seekTo(0f)
            onStop()
        }
    }

    private fun onPause() {
        musicStateChange?.onMusicPause()
        playerState.value = MusicPlayerState.Pause
    }


    private fun onStart() {
        musicStateChange?.onMusicStart()
        playerState.value = MusicPlayerState.Started
    }

    private fun onStop() {
        musicStateChange?.onMusicStop()
        playerState.value = MusicPlayerState.NotStarted
    }

    private fun onMusicEnd() {
        playerState.value = MusicPlayerState.NotStarted
    }

    private fun onSeekChange(seek: Float) {
        musicStateChange?.onSeekChange(seek)
        seekState.value = seek
    }
}