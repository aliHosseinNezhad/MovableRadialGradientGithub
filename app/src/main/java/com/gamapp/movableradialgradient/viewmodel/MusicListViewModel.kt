package com.gamapp.movableradialgradient.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.movableradialgradient.MediaController
import com.gamapp.movableradialgradient.entity.AlbumEntity
import com.gamapp.movableradialgradient.entity.ArtistEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.gamapp.movableradialgradient.entity.AudioEntity
import com.gamapp.movableradialgradient.filemanager.MusicAccessManager
import com.gamapp.movableradialgradient.utils.navigateBarHeight
import com.gamapp.movableradialgradient.utils.statusBarHeight
import kotlinx.coroutines.Dispatchers


data class Album(
    val id: Long,
    val alId: Long
)

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val application: Application,
    private val mediaController: MediaController,
    private val musicAccessManager: MusicAccessManager
) : ViewModel() {
    var mediaPlayer: MediaPlayer? = null
    private val context: Context get() = application.applicationContext
    private val contentResolver get() = application.contentResolver
    val audioList = mutableStateListOf<AudioEntity>()
    val albumList = mutableStateListOf<AlbumEntity>()
    val artistList = mutableStateListOf<ArtistEntity>()
    val loadState = mutableStateOf(false)
    fun statusBarHeight() = statusBarHeight(context)
    fun navigationBarHeight() = navigateBarHeight(context)
    fun loadMusicsByAlbumId() = viewModelScope.launch(Dispatchers.IO) {
        audioList.clear()
        audioList.addAll(musicAccessManager.getMusicList())
    }

    fun loadAlbums() = viewModelScope.launch(Dispatchers.IO) {
        albumList.clear()
        albumList.addAll(musicAccessManager.getAlbumList())
    }

    fun loadArtists() = viewModelScope.launch(Dispatchers.IO) {
        artistList.clear()
        artistList.addAll(musicAccessManager.getArtistList())
    }

    fun loadMusicsByAlbumId(albumId: Long) = viewModelScope.launch(Dispatchers.IO) {
        audioList.clear()
        audioList.addAll(musicAccessManager.getMusicsByAlbumId(albumId))
    }

    fun loadMusicsByArtistId(artistId: Long) = viewModelScope.launch(Dispatchers.IO) {
        audioList.clear()
        audioList.addAll(musicAccessManager.getMusicsByArtistId(artistId))
    }
}