package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.SIZE
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.media.MediaMetadataRetriever
import androidx.compose.runtime.MutableState


data class Audio(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
    val byteArray: MutableState<ByteArray?> = mutableStateOf(null)
)

data class Album(
    val id: Long,
    val alId: Long
)

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    var mediaPlayer: MediaPlayer? = null
    private val context: Context get() = application.applicationContext
    private val contentResolver get() = application.contentResolver
    val audioList = mutableStateOf(ArrayList<Audio>())
    val loadState = mutableStateOf(false)
    private fun extractMusicList() {
        val list = mutableListOf<Audio>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
        )
//
//        val selection = "${MediaStore.Audio.Albums.DURATION} >= ?"
//        val selectionArgs = arrayOf(
//            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//        )
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
//            selection,
//            selectionArgs,
//            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
//            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
//                val albumId = cursor.getLong(albumIdColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
//                val albumId = cursor.getLong(albumIdColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                list += Audio(id, contentUri, name, duration, size)
//                list += Album(id,albumId)
            }
        }
        audioList.value = list as ArrayList<Audio>
    }

    fun extractMusics() {
        viewModelScope.launch {
            loadState.value = true
            extractMusicList()
            loadState.value = false
        }
    }

    fun playMusic(index: Int) {
        val item = audioList.value[index]
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            item.id
        )
        mediaPlayer = MediaPlayer.create(context, contentUri)
        mediaPlayer?.start()
    }

}