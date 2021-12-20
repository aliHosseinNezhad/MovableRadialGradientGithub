package com.gamapp.movableradialgradient.ui.screen

import android.annotation.SuppressLint
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.entity.AlbumEntity
import com.gamapp.movableradialgradient.ui.theme.light
import com.gamapp.movableradialgradient.viewmodel.MusicListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <E> List<E>.pairIndex(step: Int = 2, group: (List<E?>).() -> Unit) {
    for (i in indices step step) {
        val list = mutableListOf<E?>()
        for (j in i until i + step) {
            val item = if (j <= lastIndex) this[j] else null
            list.add(item)
        }
        group(list)
    }
}

@ExperimentalMaterialApi
@Composable
fun RowScope.AlbumItem(item: AlbumEntity?) {
    val context = LocalContext.current
    val colors = Color.Gray
    if (item != null) {
        Column(modifier = Modifier.weight(1f)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(15),
                elevation = 8.dp,
                backgroundColor = Color.DarkGray,
                onClick = {}
            ) {
                var bitmap by remember {
                    mutableStateOf(null as Bitmap?)
                }
                LaunchedEffect(key1 = item.imageId) {
                    item.imageId?.let { imageId ->
                        launch(Dispatchers.IO) {
                            val mmr = MediaMetadataRetriever()
                            mmr.setDataSource(
                                context, ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    imageId
                                )
                            )
                            val byteArray = mmr.embeddedPicture
                            byteArray?.let {
                                bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            }
                        }

                    }
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: let {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.round_music_note_24),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .fillMaxSize(0.6f)
                                .align(Alignment.Center),
                        )
                    }
                }
            }
            Text(
                text = item.albumName,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = light
            )
            var rowWidth by remember {
                mutableStateOf(0.dp)
            }
            var textWidth by remember {
                mutableStateOf(0.dp)
            }
            val density = LocalDensity.current.density
            Row(
                Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        rowWidth = (it.width / density).dp
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.albumName,
                    textAlign = TextAlign.End,
                    modifier = Modifier.widthIn(max = rowWidth - textWidth),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = colors
                )

                Text(
                    text = " | " + item.count.toString() + " track${if (item.count > 1) "s" else ""}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.onSizeChanged {
                        textWidth = (it.width / density).dp
                    },
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = colors
                )
            }

        }
    } else {
        Spacer(modifier = Modifier.weight(1f))
    }
}

@ExperimentalMaterialApi
@Composable
fun Albums(musicListViewModel: MusicListViewModel) {
    val albums = musicListViewModel.albumList
    LaunchedEffect(key1 = "start") {
        musicListViewModel.loadAlbums()
    }
    LazyColumn {
        item {
            Spacer(modifier = Modifier.padding(musicListViewModel.statusBarHeight() / 2f + 16.dp))
        }
        albums.pairIndex(2) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    forEach {
                        Spacer(modifier = Modifier.padding(4.dp))
                        AlbumItem(
                            item = it
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }

}


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.shared(tag: String) = composed {
    var offset by remember {
        mutableStateOf(null as Offset?)
    }
    onGloballyPositioned {
        offset = it.positionInRoot()
    }
    DisposableEffect(key1 = tag) {
        onDispose { }
    }
    this
}