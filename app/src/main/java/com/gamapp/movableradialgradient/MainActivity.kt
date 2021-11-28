package com.gamapp.movableradialgradient

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.RenderNode
import android.os.Build
import android.os.Bundle
import android.renderscript.RenderScript
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamapp.movableradialgradient.ui.Screens
import com.gamapp.movableradialgradient.ui.screen.MusicList
//import com.gamapp.movableradialgradient.ui.screen.MusicList
import com.gamapp.movableradialgradient.ui.screen.MusicPlayer
import com.gamapp.movableradialgradient.ui.screen.PermissionScreen
import com.gamapp.movableradialgradient.ui.theme.MovableRadialGradientTheme
import com.gamapp.movableradialgradient.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @ExperimentalComposeUiApi
    @SuppressLint("NewApi")
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen(this)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun Screen(activity: MainActivity) {
    val route = if (activity.isPermissionGranted()) Screens.Player.name
    else Screens.Permission.name
    val statusBarHeight = statusBarHeight(activity)
    val navigationBarHeight = navigateBarHeight(activity)
    val paddings = Paddings(statusBarHeight, navigationBarHeight)
    Log.i("navigationBarHeight", "$navigationBarHeight")
    val navController = rememberNavController()
    StatusBarColor(isSystemInDarkTheme())
    MovableRadialGradientTheme(darkTheme = isSystemInDarkTheme()) {
        NavHost(
            navController = navController,
            startDestination = route
        ) {
            composable(route = Screens.Permission.name) {
                PermissionScreen(navController, paddings)
            }
            composable(route = Screens.Player.name) {
                MusicList()
            }
        }
    }
}


fun Activity.isPermissionGranted(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
    } else true
}



