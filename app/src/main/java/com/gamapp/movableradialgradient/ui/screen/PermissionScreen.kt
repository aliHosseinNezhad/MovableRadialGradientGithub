package com.gamapp.movableradialgradient.ui.screen

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.gamapp.movableradialgradient.R
import com.gamapp.movableradialgradient.ui.Screens
import com.gamapp.movableradialgradient.utils.Paddings
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.gamapp.movableradialgradient.isPermissionGranted
import com.gamapp.movableradialgradient.ui.theme.primary
import com.google.common.reflect.Reflection.getPackageName


@Composable
fun ColumnScope.Contents() {
    val color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Welcome to Music Player",
            modifier = Modifier.fillMaxWidth(0.8f),
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Music Player also needs the following permissions:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
    Spacer(modifier = Modifier.padding(16.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.outline_storage_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier
                .size(30.dp)
                .align(CenterVertically)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            Modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Storage",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Access storage to play music files.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BoxScope.Button(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .align(BottomCenter)
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            backgroundColor = primary,
            elevation = 2.dp,
            modifier = Modifier
                .width(170.dp)
                .height(50.dp)
                .align(Center)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Start",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Center),
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@ExperimentalComposeUiApi
@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalMaterialApi
@Composable
fun PermissionScreen(navController: NavController, paddings: Paddings) {
    val activity = LocalContext.current as Activity
    LaunchedEffect(key1 = activity.isPermissionGranted()) {
        if (activity.isPermissionGranted()) {
            navController.popBackStack()
            navController.navigate(route = Screens.Player.name)
        }
    }
    val showPermissionRationalDialog = remember {
        mutableStateOf(false)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        var total = true
        it.forEach { item ->
            total = total && item.value
        }
        if (total) {
            navController.popBackStack()
            navController.navigate(route = Screens.Player.name)
        } else {
            showPermissionRationalDialog.value = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddings.top, bottom = paddings.bottom)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .align(CenterStart)
                .padding(horizontal = 32.dp, vertical = 20.dp)
        ) {
            Contents()
        }
        Button {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_MEDIA_LOCATION)) {
                showPermissionRationalDialog.value = true
            } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionRationalDialog.value = true
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_MEDIA_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }
    PermissionDialog(showPermissionRationalDialog, activity)
}

@ExperimentalComposeUiApi
@Composable
fun PermissionDialog(showPermissionRationalDialog: MutableState<Boolean>, activity: Activity) {
    var show by showPermissionRationalDialog
    if (show)
        WarningDialog(
            modifier = Modifier
                .width(400.dp)
                .padding(horizontal = 16.dp),
            title = "Access Media Storage",
            message = "Music Player need to access media files to play music.",
            onAccept = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            },
            onDismissRequest = {
                activity.finish()
            })
}


@ExperimentalComposeUiApi
@Composable
fun WarningDialog(
    modifier: Modifier,
    title: String,
    message: String,
    onAccept: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(25.dp),
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    listOf(title to 20.sp, message to 15.sp).forEach {
                        Spacer(modifier = Modifier.padding(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Center
                        ) {
                            Text(
                                text = it.first,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = color,
                                fontSize = it.second,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .height(40.dp)
                    ) {
                        listOf(
                            "Access" to onAccept to primary,
                            "Cancel" to onDismissRequest to Color.Gray
                        ).forEach {
                            Card(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                backgroundColor = it.second,
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { it.first.second() },
                                    contentAlignment = Center,
                                ) {
                                    Text(
                                        text = it.first.first,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }


                }
            }
        }
    }
}
