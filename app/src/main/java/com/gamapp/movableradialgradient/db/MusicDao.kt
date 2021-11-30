package com.gamapp.movableradialgradient.db

import androidx.room.*
import com.gamapp.movableradialgradient.entity.MusicEntity

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(musicEntity: MusicEntity)

    @Delete
    suspend fun delete(musicEntity: MusicEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(musicEntity: MusicEntity)

    @Query("select * from MusicEntity")
    suspend fun getAll():List<MusicEntity>

    @Query("select * from MusicEntity where favorite = 1")
    suspend fun getFavorites():List<MusicEntity>

}