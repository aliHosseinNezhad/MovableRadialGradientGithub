package com.gamapp.movableradialgradient.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamapp.movableradialgradient.ui.screen.shared
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

typealias Receiver = (context: Context, intent: Intent) -> Unit

fun receiver(receiver: Receiver): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null)
                receiver(context, intent)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun TestMotion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable(route = "start") {
            Start(navController)
        }
        composable(route = "end") {
            End(navController)
        }
    }


}

@ExperimentalMaterialApi
@Composable
fun Item(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        elevation = 8.dp,
        onClick = onClick
    ) {

    }
}


@ExperimentalMaterialApi
@Composable
fun End(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    ) {
        Item(
            modifier = Modifier
                .size(400.dp)
                .align(Center)
                .motion("card", "start", "end")
        ) {
            navController.popBackStack(route = "start", inclusive = false, saveState = false)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun Start(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Item(
            modifier = Modifier
                .size(200.dp)
                .align(TopStart)
                .motion("card", "end", "start")
        ) {
            navController.navigate("end")
        }
    }
}


fun <K> MutableMap<K, MutableMap<K, Object>>.difference(category: K, from: K, current: K): Offset? {
    val startOffset = this(category, from)
    val endOffset = this(category, current)
    return if (startOffset != null && endOffset != null) {
        startOffset.offset - endOffset.offset
    } else null
}


fun <K, V> MutableMap<K, MutableMap<K, V>>.update(key1: K, key2: K, value: V) {
    val category = this[key1] ?: mutableMapOf()
    this[key1] = category
    category[key2] = value
}

operator fun <K, V> MutableMap<K, MutableMap<K, V>>.set(key1: K, key2: K, value: V) =
    update(key1, key2, value)

operator fun <K, V> MutableMap<K, MutableMap<K, V>>.invoke(key1: K, key2: K): V? {
    return this[key1]?.get(key2)
}

data class Object(val offset: Offset, val size: IntSize)


internal val data = mutableMapOf<Any, MutableMap<Any, Object>>()

operator fun Offset.minus(offset: Offset): Offset {
    return Offset(offset.x - x, offset.y - y)
}

fun Modifier.motion(category: Any, from: Any, item: Any) = composed {
    var coroutineScope = rememberCoroutineScope()
    var offset by remember {
        mutableStateOf(IntOffset(Offset.Zero.x.toInt(), Offset.Zero.y.toInt()))
    }
    var scale by remember {
        mutableStateOf(1f)
    }
    var difference by remember {
        mutableStateOf(null as Offset?)
    }
    var alpha by remember {
        mutableStateOf(data[from]?.let { 0f }?:let { 1f })
    }
    LaunchedEffect(key1 = difference) {
        difference?.let { difference ->
            coroutineScope.launch {
                var count = 1f
                val scale1 = data(category, from)?.size!!
                val scale2 = data(category, item)?.size!!
                val x = scale1.width.toFloat() / scale2.width
                val y = scale1.height.toFloat() / scale2.height
                while (count >= 0f) {
                    delay(1)
                    val value = sin(PI / 2f * count)
                    scale = ((1 - value).toFloat() * (1 - x) + x)
                    offset = IntOffset(
                        (value * difference.x + (scale1.width - scale2.width) * value/2f).toInt(),
                        (value * difference.y + (scale1.height - scale2.height) * value/2f).toInt()
                    )
                    alpha = 1f
                    count -= 0.005f
                }
            }
        }
    }
    onGloballyPositioned {
        data[category, item] = Object(it.positionInRoot(), it.size)
        difference = data.difference(category, from, item)
    }.graphicsLayer {
        this.alpha = alpha
        this.scaleX = scale
        this.scaleY = scale
        this.translationY = offset.y.toFloat()
        this.translationX = offset.x.toFloat()
    }
}