package com.gamapp.movableradialgradient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamapp.movableradialgradient.model.Coordinate
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import com.gamapp.movableradialgradient.ui.theme.MovableRadialGradientTheme
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovableRadialGradientTheme {
                MotionRadialGradient(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    items = listOf(
                        RadialGradientInfo(
                            color = Color.Blue,
                            radiusPercent = 1f,
                            speed = 1f,
                            motionPath = {
                                sin(2 * it)
                            },
                            center = Coordinate(0.5f, 0.5f)
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MovableRadialGradientTheme {
        Greeting("Android")
    }
}