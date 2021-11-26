package com.gamapp.movableradialgradient.ui.screen

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gamapp.movableradialgradient.viewmodel.MusicListViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Context.LoadImage(
    id: Long,
    byteArray: MutableState<ByteArray?>
) {
    val coroutineScope = rememberCoroutineScope()
    if (byteArray.value == null)
        DisposableEffect(key1 = "start") {

            coroutineScope.launch(context = Dispatchers.IO) {
                val  mmr = MediaMetadataRetriever()
                mmr.setDataSource(
                    this@LoadImage, ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                )
                val data = mmr.embeddedPicture
                byteArray.value = data
            }
            onDispose {
//                byteArray.value = null
            }
        }
}


@SuppressLint("NewApi")
@Composable
fun MusicList(viewModel: MusicListViewModel = hiltViewModel()) {
    val holder = ImageBitmap(50, 50)
    val canvas = Canvas(holder)
    val mmr = MediaMetadataRetriever()
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
                    context.LoadImage(item.id, item.byteArray)
                    item.byteArray.value?.let {
                        GlideImage(
                            imageModel = it,
                            modifier = Modifier
                                .size(50.dp),
                            requestOptions = {
                                RequestOptions()
                                    .override(50, 50)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                            },
                        )
                    } ?: let {

                    }
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