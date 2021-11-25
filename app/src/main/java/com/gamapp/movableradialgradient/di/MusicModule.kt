package com.gamapp.movableradialgradient.di

import android.content.Context
import android.media.MediaPlayer
import com.gamapp.movableradialgradient.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MusicModule{
    @Provides
    @Singleton
    fun provideMediaPlayer(@ApplicationContext context: Context): MediaPlayer? {
        return MediaPlayer.create(context, R.raw.hozier_take_me_to_church)
    }
}