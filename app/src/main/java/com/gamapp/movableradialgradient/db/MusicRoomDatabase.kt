package com.gamapp.movableradialgradient.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gamapp.movableradialgradient.entity.MusicEntity

@Database(entities = [MusicEntity::class], version = 1, exportSchema = false)
abstract class MusicRoomDatabase : RoomDatabase() {
    abstract fun musicDao():MusicDao
}