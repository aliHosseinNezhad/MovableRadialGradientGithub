package com.gamapp.movableradialgradient.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.gamapp.movableradialgradient.Constant
import android.app.PendingIntent
import android.media.MediaPlayer

import com.gamapp.movableradialgradient.MainActivity
import com.gamapp.movableradialgradient.MediaContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MusicService : Service() {
    @Inject
    lateinit var mediaContainer: MediaContainer


    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val binder = MusicServiceBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification =
            NotificationCompat.Builder(this, Constant.NOTIFICATION_CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(Constant.NOTIFICATION_CHANNEL_NAME)
                .setChannelId(Constant.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(Constant.MUSIC_PLAYER_NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }


    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }
}