package com.gamapp.movableradialgradient.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

typealias Receiver = (context: Context, intent: Intent) -> Unit

fun receiver(receiver: Receiver): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null)
                receiver(context, intent)
        }
    }
}

