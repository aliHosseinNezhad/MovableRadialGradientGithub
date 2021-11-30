package com.gamapp.movableradialgradient.di

import android.content.Context
import androidx.room.Room
import com.gamapp.movableradialgradient.db.MusicDao
import com.gamapp.movableradialgradient.db.MusicRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context): MusicRoomDatabase {
        return Room.databaseBuilder(
                context,
                MusicRoomDatabase::class.java,
                "music_room_database"
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideMusicDao(musicRoomDatabase: MusicRoomDatabase): MusicDao {
        return musicRoomDatabase.musicDao()
    }
}