package com.example.customgallery.ui.screen.splash_screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.customgallery.R
import com.example.customgallery.ui.theme.CustomGalleryTheme

@Composable
fun SplashScreen(showTheProgressBar: Boolean, onGrantedThePermission: () -> Unit) {
    val listOfPermissions = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val isNotGranted =
                listOfPermissions.any { permission -> result[permission] == false }
            if (isNotGranted)
                Toast.makeText(
                    context,
                    "You need to grant all permissions",
                    Toast.LENGTH_SHORT
                ).show()
        }
    )
    var showRequestingPermissionButton by remember { mutableStateOf(false) }
    ObserveRequestingPermissionStatus(
        listOfPermissions = listOfPermissions,
        permissionStatus = { granted ->
            if (granted)
                onGrantedThePermission()
            else
                showRequestingPermissionButton = true
        }
    )
    SplashScreenContent(
        showTheProgressBar = showTheProgressBar,
        showRequestingPermissionButton = showRequestingPermissionButton,
        onClickRequestingPermissionButton = {
            activity?.let {
                listOfPermissions.forEach { permission ->
                    if (shouldShowRequestPermissionRationale(activity, permission))
                        permissionRequest.launch(listOfPermissions.toTypedArray())
                    else
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            this.data =
                                Uri.fromParts("package", context.packageName, null)
                        }.also {
                            activity.startActivity(it)
                        }
                }
            }
        }
    )
}

@Composable
fun SplashScreenContent(
    showTheProgressBar: Boolean,
    showRequestingPermissionButton: Boolean,
    onClickRequestingPermissionButton: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(500.dp)
                .aspectRatio(1f)
        )

        AnimatedVisibility(showTheProgressBar, modifier = Modifier.align(Alignment.Center)) {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Loading The Data...", fontSize = 13.sp)
            }
        }

        AnimatedVisibility(
            visible = showRequestingPermissionButton,
            enter = slideInVertically(tween(durationMillis = 400)), modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        ) {
            Button(
                onClick = onClickRequestingPermissionButton,
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Request Permission")
            }
        }
    }
}

@Composable
fun ObserveRequestingPermissionStatus(
    listOfPermissions: List<String>,
    permissionStatus: (Boolean) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                permissionStatus(listOfPermissions.all { permission ->
                    checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                })
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    CustomGalleryTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SplashScreen(
                onGrantedThePermission = {}, showTheProgressBar = true
            )
        }
    }
}