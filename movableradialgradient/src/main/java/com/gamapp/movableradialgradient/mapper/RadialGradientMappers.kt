package com.gamapp.movableradialgradient.mapper

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Rect
import com.gamapp.movableradialgradient.model.RadialGradientInfo
import com.gamapp.movableradialgradient.model.RadialGradientMotionInfo
import kotlin.math.max


internal fun RadialGradientInfo.toMotionMapper(
    rect: Rect
): RadialGradientMotionInfo {
    val maxDimension = max(rect.width, rect.height)
    return RadialGradientMotionInfo(
        color = this.color,
        radius = this.radiusPercent * maxDimension,
        motionPath = this.motionPath,
        speed = this.speed,
        coordinate = null,
        center = this.center,
        radiusDomain = this.radiusDomain
    )
}

internal fun List<RadialGradientInfo>.toMotionListMapper(rect: Rect): SnapshotStateList<RadialGradientMotionInfo> {
    val list = SnapshotStateList<RadialGradientMotionInfo>()
    this.forEach {
        list.add(it.toMotionMapper(rect))
    }
    return list
}
