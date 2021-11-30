package com.gamapp.movableradialgradient.di

import android.app.UiModeManager
import android.content.Context
import androidx.activity.ComponentActivity
import com.gamapp.movableradialgradient.MediaController
import com.gamapp.movableradialgradient.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MusicModule {
//    @Provides
//    @Singleton
//    fun provideMediaPlayer(
//        @ApplicationContext context: Context,
//        musicRepository: MusicRepository
//    ): MediaController {
//        return MediaController(context, musicRepository)
//    }


    @Provides
    @Singleton
    fun provideUiModeManager(@ApplicationContext context: Context): UiModeManager {
        return context.getSystemService(ComponentActivity.UI_MODE_SERVICE) as UiModeManager
    }
}