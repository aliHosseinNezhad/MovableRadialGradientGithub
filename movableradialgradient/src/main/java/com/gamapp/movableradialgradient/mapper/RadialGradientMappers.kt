package com.gamapp.movableradialgradient.mapper

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import com.gamapp.movableradialgradient.*
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import com.gamapp.movableradialgradient.model.RadialGradientMotionInfo
import kotlin.math.max

internal fun RadialGradientInfo.toMotionMapper(
    rect: Rect
): RadialGradientMotionInfo {
    val center = Offset(rect.width * center.percentX, rect.height * center.percentY)
    val maxDimension = max(rect.width, rect.height)
    val radius = this.radiusPercent * maxDimension
    val colorStops = generateColorStops(1f / radius)
    val colorList = colorStops.extractList {
        this.color.alpha((it).shadow())
    }
    val paint = AndroidPaint().apply {
        isAntiAlias = true
        shader = RadialGradientShader(
            center = Offset(center.x + radius / 2f, center.y + radius / 2f),
            radius = if (radius <= 0) 1f else radius,
            colorStops = if (colorStops.size <= 1) listOf(0f, 1f) else colorStops,
            colors = if (colorList.size <= 1) listOf(Color.White, Color.White) else colorList
        )
    }
    return RadialGradientMotionInfo(
        color = color,
        paint = paint,
        radius = radius,
        motionPath = this.polarMotionPath,
        speed = this.speed,
        coordinate = null,
        center = center,
        radiusDomain = this.radiusDomain,
        motionRadiusPercent = this.motionFieldSizePercent
    )
}

internal fun List<RadialGradientInfo>.toMotionListMapper(rect: Rect): SnapshotStateList<RadialGradientMotionInfo> {
    val list = SnapshotStateList<RadialGradientMotionInfo>()
    this.forEach {
        list.add(it.toMotionMapper(rect))
    }
    return list
}


internal fun SnapshotStateList<RadialGradientMotionInfo>.updateWith(
    list: List<RadialGradientMotionInfo>,
    rect: Rect
) {
    if (this.size > list.size) {
        (list.size until this.size).forEach {
            this.removeLast()
        }
    }
    for (i in this.indices) {
        this[i].updateWith(list[i]) {
            this.update(this[i], it)
        }
    }
    if (this.size < list.size) {
        for (i in this.size until list.size) {
            this.add(list[i])
        }
    }
}

internal fun RadialGradientMotionInfo.updateWith(
    item: RadialGradientMotionInfo,
    update: ((RadialGradientMotionInfo).() -> Unit) -> Unit
) {
    update {
        center = item.center
        speed = item.speed
        paint = item.paint
        radius = item.radius
        motionPath = item.motionPath
        color = item.color
        radiusDomain = item.radiusDomain
        motionRadiusPercent = item.motionRadiusPercent
    }
}