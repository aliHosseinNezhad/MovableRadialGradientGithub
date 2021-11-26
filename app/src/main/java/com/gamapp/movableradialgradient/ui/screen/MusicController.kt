package com.gamapp.movableradialgradient.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

