package com.gamapp.movableradialgradient.ui.screen

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.viewmodel.MusicListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Context.LoadImage(
    id: Long,
    modifier: Modifier = Modifier.size(100.dp)
) {
    var imageBitmap by remember {
        mutableStateOf(null as ImageBitmap?)
    }
    val coroutineScope = rememberCoroutineScope()
    if (imageBitmap == null)
        DisposableEffect(key1 = "start") {
            val job = coroutineScope.launch(context = Dispatchers.IO) {
                try {
                    val mmr = MediaMetadataRetriever()
                    mmr.setDataSource(
                        this@LoadImage, ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                    )
                    val data = mmr.embeddedPicture
                    data?.let { bytes ->
                        var bitmap =
                            BitmapFactory.decodeByteArray(
                                bytes,
                                0,
                                bytes.size
                            )
                        val width = bitmap.width
                        val height = bitmap.height
                        bitmap = bitmap.scale(100, (100 * height / width.toFloat()).toInt())
                        imageBitmap = bitmap?.asImageBitmap()
                    }
                    mmr.release()
                } catch (e: Exception) {

                }
            }
            onDispose {
                coroutineScope.launch {
                    job.cancelAndJoin()
                }
            }
        }
    imageBitmap?.let { imageBitmap ->
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } ?: let {
        Image(
            painter = painterResource(id = R.drawable.round_music_note_24),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}


@SuppressLint("NewApi")
@Composable
fun MusicList(viewModel: MusicListViewModel = hiltViewModel()) {
    val holder = ImageBitmap(50, 50)
    val canvas = Canvas(holder)
    canvas.nativeCanvas.drawColor(Color.Blue.toArgb())
    val musicList by viewModel.audioList
    val loadState by viewModel.loadState
    val context = LocalContext.current
    DisposableEffect(key1 = "start") {
        viewModel.extractMusics()
        onDispose { }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(musicList) { index, item ->
                Spacer(modifier = Modifier.padding(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            viewModel.playMusic(index)
                        }
                ) {
                    Text(text = "$item.id ", color = Color.Gray)
                    context.LoadImage(item.id)
                }
            }
            item { Spacer(modifier = Modifier.padding(8.dp)) }
        }
        if (loadState)
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )
    }

}