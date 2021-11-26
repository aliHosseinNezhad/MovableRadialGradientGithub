package com.gamapp.movableradialgradient.di

import android.app.UiModeManager
import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import com.gamapp.movableradialgradient.MediaContainer
import com.gamapp.movableradialgradient.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MusicModule {
    @Provides
    @Singleton
    fun provideMediaPlayer(@ApplicationContext context: Context): MediaContainer {
        return MediaContainer(context)
    }


    @Provides
    @Singleton
    fun provideUiModeManager(@ApplicationContext context: Context): UiModeManager {
        return context.getSystemService(ComponentActivity.UI_MODE_SERVICE) as UiModeManager
    }
}