package com.gamapp.movableradialgradient

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamapp.movableradialgradient.ui.Screens
import com.gamapp.movableradialgradient.ui.screen.MusicList
import com.gamapp.movableradialgradient.ui.screen.MusicPlayer
import com.gamapp.movableradialgradient.ui.screen.PermissionScreen
import com.gamapp.movableradialgradient.ui.theme.MovableRadialGradientTheme
import com.gamapp.movableradialgradient.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var statusBarHeight: Dp = 0.dp
    var navigationBarHeight: Dp = 0.dp

    @ExperimentalComposeUiApi
    @SuppressLint("NewApi")
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route = if (isPermissionGranted()) Screens.Player.name
        else Screens.Permission.name
        statusBarHeight = statusBarHeight(this)
        navigationBarHeight = navigateBarHeight(this)
        val paddings = Paddings(statusBarHeight, navigationBarHeight)
        Log.i("navigationBarHeight", "$navigationBarHeight")

        setContent {
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
//                        MusicPlayer(
//                            statusBarHeight = statusBarHeight,
//                            navigationBarHeight = navigationBarHeight
//                        )
                        MusicList()
                    }
                }
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



