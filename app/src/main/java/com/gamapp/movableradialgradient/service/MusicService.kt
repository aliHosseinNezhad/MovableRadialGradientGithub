package com.gamapp.movableradialgradient.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gamapp.movableradialgradient.*

import com.gamapp.movableradialgradient.utils.receiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


interface MusicStateChange {
    fun onMusicStart()
    fun onMusicPause()
    fun onMusicStop()
    fun onSeekChange(seek: Float)
}

@AndroidEntryPoint
class MusicService : Service(), MusicStateChange {
    @Inject
    lateinit var mediaController: MediaController

    private lateinit var playerView :RemoteViews

    private val buttonClickReceiver = receiver { context, intent ->
        when (intent.getStringExtra(Constant.BUTTON_CLICKED_MSG) ?: "no message") {
            Constant.CLOSE_INTENT_ACTION -> {
                mediaController.stop()
                stopSelf()
            }
            Constant.PLAY_PAUSE_INTENT_ACTION -> {
                mediaController.playerState.value.apply {
                    when {
                        this == MusicPlayerState.Started -> {
                            mediaController.pause()
                        }
                        this == MusicPlayerState.Pause -> {
                            mediaController.resume()
                        }
                        else -> {
                            mediaController.play()
                        }
                    }
                }

            }

        }
    }

    override fun onCreate() {
        super.onCreate()
        playerView = RemoteViews(packageName,R.layout.music_notification)
        registerReceiver(buttonClickReceiver, IntentFilter(Constant.BUTTONS_BROADCAST_INTENT_KEY));
        mediaController.setOnStateChangeListener(this)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intents = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intents)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        setListeners()
        val notification: Notification = createNotification(pendingIntent)
        startForeground(Constant.MUSIC_PLAYER_NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    private fun createNotification(pendingIntent:PendingIntent): Notification {
        return NotificationCompat.Builder(applicationContext, Constant.NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setColor(Color(0, 100, 255).toArgb())
            .setColorized(true)
            .setContentTitle("MusicName")
            .setContentText("music player is playing music")
            .setSmallIcon(R.drawable.round_music_note_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(playerView)
//                .setCustomBigContentView()
            .build()
    }

    private fun setListeners() {
        //close btn
        val closeClickedIntent = Intent(Constant.BUTTONS_BROADCAST_INTENT_KEY)
        closeClickedIntent.putExtra(Constant.BUTTON_CLICKED_MSG, Constant.CLOSE_INTENT_ACTION)
        val closeClickedPIntent = PendingIntent.getBroadcast(
            this,
            Constant.CLOSE_INTENT_REQUEST_CODE,
            closeClickedIntent,
            0
        )
        playerView.setOnClickPendingIntent(R.id.exit_music_player, closeClickedPIntent)

        //pause - play

        val playPauseIntent = Intent(Constant.BUTTONS_BROADCAST_INTENT_KEY)
        playPauseIntent.putExtra(Constant.BUTTON_CLICKED_MSG, Constant.PLAY_PAUSE_INTENT_ACTION)
        val playPauseClickedIntent = PendingIntent.getBroadcast(
            this,
            Constant.PLAY_PAUSE_INTENT_REQUEST_CODE,
            playPauseIntent,
            0
        )
        playerView.setOnClickPendingIntent(R.id.play_pause_btn, playPauseClickedIntent)
    }

    override fun onMusicStart() {
        playerView.setImageViewResource(R.id.play_pause_btn,R.drawable.round_play_arrow_24)
    }

    override fun onMusicPause() {
        playerView.setImageViewResource(R.id.play_pause_btn,R.drawable.round_pause_24)
    }

    override fun onMusicStop() {

    }

    override fun onSeekChange(seek: Float) {

    }
}