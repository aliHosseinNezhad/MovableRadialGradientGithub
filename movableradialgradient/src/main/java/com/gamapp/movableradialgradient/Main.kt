package com.gamapp.movableradialgradient

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import com.gamapp.movableradialgradient.mapper.toMotionListMapper
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import com.gamapp.movableradialgradient.model.RadialGradientMotionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*


internal suspend fun affectAnimation(
    item: RadialGradientMotionInfo,
    rect: Rect,
    enable: Boolean,
    update: (update: (RadialGradientMotionInfo) -> Unit) -> Unit
) {
    val cx = item.center.x
    val cy = item.center.y
    while (true) {
        update {
            if (item.count % 6000 == 0f)
                item.count = 0f
            val a = (item.radiusDomain.getDifference())
            val b = (item.count % 6000) / 6000f
            val angle = a * b
            val radius =
                item.motionPath(angle) * min(rect.width, rect.height) * item.motionRadiusPercent
            it.coordinate = Offset(cx + radius * cos(angle), cy + radius * sin(angle))
            item.count += item.speed
        }
//        delay(1)
        if (enable)
            delay(1)
        else {
            delay(5)
            break
        }
    }
}

internal fun SnapshotStateList<RadialGradientMotionInfo>.update(
    item: RadialGradientMotionInfo,
    update: (RadialGradientMotionInfo).() -> Unit
) {
    val index = indexOf(item)
    if (index != -1) {
        update(item)
        val newItem = item.copy()
        this[index] = newItem
    }
}

@Composable
fun MotionRadialGradient(
    modifier: Modifier,
    items: List<RadialGradientInfo>,
    enable: Boolean,
    rect:Rect
) {
    Box(
        modifier = modifier
    ) {
        if (rect.width > 0f && rect.height > 0f)
            MotionRadialGradientCanvas(
                rect = rect,
                items = items.toMotionListMapper(rect),
                enable = enable
            )
    }
}

@Composable
internal fun MotionRadialGradientCanvas(
    rect: Rect,
    items: SnapshotStateList<RadialGradientMotionInfo>,
    enable: Boolean
) {
    val itemsState = remember {
        items
    }
    LaunchedEffect(key1 = enable) {
        for (item in itemsState) {
            launch {
                affectAnimation(item, rect, enable = enable) {
                    itemsState.update(item, it)
                }
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .drawBehind {
            drawContext.canvas.apply {
                itemsState.forEach { item ->
                    item.coordinate?.let { offset ->
                        gradientCircle(
                            offset = offset,
                            radius = item.radius,
                            color = item.color
                        )
                    }
                }
            }
        }
    )
}

fun Color.alpha(value: Float): Color {
    return Color(
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt(),
        (this.alpha * value * 255).toInt()
    )
}

fun <E, V> List<E>.extractList(generator: (E) -> V): List<V> {
    val list = ArrayList<V>()
    forEach {
        list.add(generator(it))
    }
    return list
}

fun Float.shadow(): Float {
    return exp(this * this * -1f * 12f) * 0.8f
}

fun generateColorStops(value: Float = 0.1f): List<Float> {
    val list = ArrayList<Float>()
    var counter = 0f
    while (counter <= 1f) {
        list.add(counter)
        counter += value
    }
    return list
}

fun Canvas.gradientCircle(offset: Offset, radius: Float, color: Color) {
    Log.i("radiusTest", "gradientCircle: $radius")
    val colorStops = generateColorStops(1f / radius)
    val paint = AndroidPaint().apply {
        isAntiAlias = true
        shader = RadialGradientShader(
            center = offset,
            radius = radius,
            colorStops = colorStops,
            colors = colorStops.extractList {
                color.alpha((it).shadow())
            }
        )
    }
    drawCircle(center = offset, radius = radius, paint = paint)
}