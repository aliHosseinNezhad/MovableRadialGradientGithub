package com.gamapp.movableradialgradient.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.gamapp.movableradialgradient.Constant
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.TaskStackBuilder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import com.gamapp.movableradialgradient.MainActivity
import com.gamapp.movableradialgradient.MediaContainer
import com.gamapp.movableradialgradient.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openjdk.tools.javac.Main
import javax.inject.Inject
import kotlin.math.min


@AndroidEntryPoint
class MusicService : Service() {
    @Inject
    lateinit var mediaContainer: MediaContainer


    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val binder = MusicServiceBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intents = Intent(applicationContext, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intents,
            PendingIntent.FLAG_IMMUTABLE
        )
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.Blue.toArgb())
        canvas.apply {
            drawCircle(width / 2f, height / 2f, min(width, height) / 2f, Paint().apply {
                isAntiAlias = true
                color = Color.White.toArgb()
                style = Paint.Style.FILL_AND_STROKE
            })
        }
        val notificationLayout = RemoteViews(packageName, R.layout.testy)
//        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)

        val notification: Notification =
            NotificationCompat.Builder(applicationContext, Constant.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle("MusicName")
                .setContentText("music player is playing music")
                .setSmallIcon(R.drawable.round_music_note_24)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
//                .setCustomBigContentView(notificationLayoutExpanded)
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