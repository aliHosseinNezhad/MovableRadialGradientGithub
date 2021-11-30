package com.gamapp.movableradialgradient.repository

import com.gamapp.movableradialgradient.db.MusicDao
import com.gamapp.movableradialgradient.entity.MusicEntity
import com.gamapp.movableradialgradient.filemanager.MusicAccessManager
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val musicDao: MusicDao,
    private val musicAccessManager: MusicAccessManager
) {
    suspend fun getAllFavorite() = musicDao.getFavorites()
    suspend fun add(musicEntity: MusicEntity) = musicDao.create(musicEntity)
    suspend fun remove(musicEntity: MusicEntity) = musicDao.delete(musicEntity)
    suspend fun update(musicEntity: MusicEntity) = musicDao.update(musicEntity)
    suspend fun addToFavorite(musicEntity: MusicEntity) {
        val music = musicEntity.copy(favorite = true)
        musicDao.update(music)
    }
    fun getAllMusicFromFile() = musicAccessManager.getMusicList()
}