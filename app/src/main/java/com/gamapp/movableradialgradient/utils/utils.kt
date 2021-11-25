package com.gamapp.movableradialgradient.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

//fun statusBarHeight(context: Context): Dp {
//    val rectangle = Rect()
//    val window: Window = (context as Activity).window
//    window.decorView.getWindowVisibleDisplayFrame(rectangle)
//    val statusBarHeight: Int = rectangle.top
//    val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
//    val titleBarHeight = contentViewTop - statusBarHeight
//    val density = context.resources.displayMetrics.density
//    return (titleBarHeight / density).dp
//}

fun Color.isLight(): Boolean = (this.blue > 0.5f || this.green > 0.5f || this.red > 0.5f)

fun navigateBarHeight(context: Context): Dp {
    val resources: Resources = context.resources
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val height = if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 1
    val density = context.resources.displayMetrics.density
    return (height / density).dp
}

fun statusBarHeight(context: Context): Dp {
    val resources: Resources = context.resources
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    val height = if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 1
    val density = context.resources.displayMetrics.density
    return (height / density).dp
}

@Composable
fun StatusBarColor(isDarkMode: Boolean) {
    val context = LocalContext.current
    val activity = context as Activity
    val window: Window = activity.window
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(
        window,
        activity.findViewById(R.id.content)
    ).apply {
        this.isAppearanceLightNavigationBars = false
        isAppearanceLightStatusBars = !isDarkMode
    }
}

@Composable
fun activity(): Activity {
    return LocalContext.current as Activity
}

@Composable
fun context(): Context {
    return LocalContext.current
}

fun changeDarkMode(isDark: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (isDark) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
    )
}

@Composable
fun SetSystemColorByAnimation(isDarkMode: Boolean, dark: Color, light: Color) {
    StatusBarColor(isDarkMode)
}