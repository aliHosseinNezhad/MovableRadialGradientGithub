package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.app.UiModeManager
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults.AnimationSpec
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.gamapp.movableradialgradient.MediaController
import com.gamapp.movableradialgradient.alpha
import com.gamapp.movableradialgradient.entity.AudioEntity
import com.gamapp.movableradialgradient.ui.theme.primary
import com.gamapp.movableradialgradient.ui.theme.secondary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


data class MusicModel(
    var name: MutableState<String> = mutableStateOf(""),
    var details: MutableState<String> = mutableStateOf(""),
    var id: MutableState<Long?> = mutableStateOf(null),
    var bitmap: MutableState<ImageBitmap?> = mutableStateOf(null)
)

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val application: Application,
    private val mediaController: MediaController,
    private val uiModeManager: UiModeManager
) : ViewModel() {
    @ExperimentalMaterialApi
    val swipeableState = SwipeableState(
        initialValue = 1,
        animationSpec = AnimationSpec,
        confirmStateChange = { true }
    )
    val musicModel: MusicModel = MusicModel()
    val colors: MutableState<List<Color>> = initPaletteColors()
    private fun initPaletteColors(): MutableState<List<Color>> {
        val colors = mutableListOf(
            primary.alpha(0.9f),
            secondary.alpha(0.65f)
        )
        return mutableStateOf(colors)
    }

    val musicPlayState = mediaController.playerState
    val seekState: MutableState<Float> = mediaController.seekState
    private val context: Context get() = application.applicationContext

    fun init() {
        viewModelScope.launch {
            mediaController.audioEntity?.let {
                musicModel.bitmap.value = getBitmap(it.id)
                musicModel.id.value = it.id
                musicModel.name.value = it.displayName
                musicModel.bitmap.value?.let {
                    setPalette(it.asAndroidBitmap())
                } ?: let {
                    colors.value = initPaletteColors().value
                }
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

    private fun repeat() = mediaController.repeat()

    fun seekTo(seek: Float) = mediaController.seekTo(seek)

    fun pause() = mediaController.pause()


    fun resume() = mediaController.resume()

    fun play() = mediaController.play()


    fun setMusic(item: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaController.set(item)
            play()
            init()
        }
    }

    private fun setPalette(bitmap: Bitmap) {
        val palette = Palette
            .from(bitmap)
            .generate()
        val list1 = palette.targets.mapNotNull { it ->
            palette.getSwatchForTarget(it)?.rgb?.let {
                Color(it).alpha(0.8f)
            }
        }
        if (list1.size > 2) {
            colors.value = list1
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