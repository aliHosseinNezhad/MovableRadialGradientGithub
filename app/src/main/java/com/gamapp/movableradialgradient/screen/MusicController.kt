package com.gamapp.movableradialgradient.screen

import android.graphics.PorterDuff
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicViewModel

@Composable
fun MusicControllers(modifier: Modifier, viewModel: MusicViewModel = hiltViewModel()) {
    Column(modifier = modifier) {
        SeekBar(modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(60.dp))
        Spacer(modifier = Modifier.padding(8.dp))
        MusicPlayButtons(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp)
        )
    }

}

