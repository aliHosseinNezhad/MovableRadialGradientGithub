package com.gamapp.movableradialgradient.ui.screen

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.viewmodel.Audio
import com.gamapp.movableradialgradient.viewmodel.MusicListViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.lang.Exception


@Composable
fun Context.LoadImage(
    id: Long,
    modifier: Modifier
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
                        bitmap = bitmap.scale(50, (50 * height / width.toFloat()).toInt())
                        imageBitmap = bitmap.asImageBitmap()
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


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@SuppressLint("NewApi")
@Composable
fun MusicList(viewModel: MusicListViewModel = hiltViewModel()) {
    val musicPlayViewModel: MusicPlayViewModel = hiltViewModel()
    val musicList by viewModel.audioList
    DisposableEffect(key1 = "start") {
        viewModel.extractMusics()
        onDispose { }
    }
    Box(
        modifier = Modifier
            .padding(
                bottom = viewModel.navigationBarHeight() + 25.dp,
                top = viewModel.statusBarHeight()
            )
            .fillMaxSize()
    ) {
        LazyColumn {
            item {
                Spacer(modifier = Modifier.padding(32.dp))
            }
            itemsIndexed(musicList) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            musicPlayViewModel.setMusic(item)
                        }
                ) {
                    MusicItem(item)
                }
            }
            item { Spacer(modifier = Modifier.padding(60.dp)) }
        }
    }
    MusicPlayer(
        statusBarHeight = viewModel.statusBarHeight(),
        navigationBarHeight = viewModel.navigationBarHeight(),
        playViewModel = musicPlayViewModel
    )
}


@Composable
fun MusicItem(item: Audio) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Surface(
            color = Color.Gray,
            shape = RoundedCornerShape(15),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
        ) {
            context.LoadImage(
                id = item.id, modifier = Modifier
                    .fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),
            ) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 30.dp),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),
            ) {
                Text(
                    text = item.duration.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 30.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Image(
            painter = painterResource(id = R.drawable.round_more_vert_24),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxHeight()
                .width(30.dp),
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.padding(8.dp))
    }

}