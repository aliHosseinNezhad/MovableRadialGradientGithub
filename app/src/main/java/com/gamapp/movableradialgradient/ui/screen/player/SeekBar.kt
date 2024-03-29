package com.gamapp.movableradialgradient.ui.screen.player

import android.graphics.PorterDuff
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.movableradialgradient.viewmodel.MusicPlayViewModel

@Composable
fun SeekBar(modifier: Modifier, clickable:Boolean, playViewModel: MusicPlayViewModel = hiltViewModel()) {
    AndroidView(factory = {
        SeekBar(it).apply {
            max = 10000
            progressDrawable.apply {
                setColorFilter(
                    Color.White.toArgb(),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
            thumb.setColorFilter(
                Color.White.toArgb(),
                PorterDuff.Mode.SRC_ATOP
            )
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        playViewModel.seekTo(progress / max.toFloat())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                    viewModel.pause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                    viewModel.resume()
                }
            })
        }
    }, update = {
        it.isEnabled = clickable
        it.progress = (playViewModel.seekState.value * it.max).toInt()
    },
        modifier = modifier
    )
}