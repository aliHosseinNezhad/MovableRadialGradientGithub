package com.gamapp.movableradialgradient

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.delay
import kotlin.math.*


internal suspend fun affectAnimation(
    item: RadialGradientMotionInfo,
    rect: Rect,
    update: (update: (RadialGradientMotionInfo) -> Unit) -> Unit
) {
    val cx = item.center.percentX * rect.width
    val cy = item.center.percentY * rect.height
    var count = 0f
    while (true) {
        update {
            if (count % 6000 == 0f)
                count = 0f
            val a = (item.radiusDomain.getDifference())
            val b = (count % 6000) / 6000f
            val angle = a * b
            val radius = item.motionPath(angle) * min(rect.width, rect.height)
            it.coordinate = Offset(cx + radius * cos(angle), cy + radius * sin(angle))
        }
        count += item.speed
        delay(1)
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
fun MotionRadialGradient(modifier: Modifier, items: List<RadialGradientInfo>) {
    var rect by remember {
        mutableStateOf(null as Rect?)
    }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                rect = it.boundsInParent()
            }
    ) {
        rect?.let { rect ->
            MotionRadialGradientCanvas(rect = rect, items = items.toMotionListMapper(rect))
        }
    }
}

@Composable
internal fun MotionRadialGradientCanvas(
    rect: Rect,
    items: SnapshotStateList<RadialGradientMotionInfo>
) {
    val itemsState = remember {
        items
    }
    LaunchedEffect(key1 = "start") {
        for (item in itemsState) {
            affectAnimation(item, rect) {
                itemsState.update(item, it)
            }
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
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
}

fun Color.alpha(value: Float): Color {
    return Color(
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt(),
        (value * 255).toInt()
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
    return exp(this * this * -1f * 12f) * 0.7f
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

/*
// params:
// offset is coordinate of gradient circle
// radius is radius of circle
// radius must be greeter than zero
// color is color of circle
//
*/
fun Canvas.gradientCircle(offset: Offset, radius: Float, color: Color) {
    val colorStops = generateColorStops(1f/radius)
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
    drawCircle(center = offset, radius = radius, paint)
}