package com.example.customgallery.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.customgallery.ui.screen.gallery_home.GalleryHomeScreen
import com.example.customgallery.ui.screen.gallery_home.GalleryHomeViewModel
import com.example.customgallery.ui.screen.splash_screen.SplashScreen
import com.example.customgallery.ui.screen.splash_screen.SplashScreenViewModel

@Composable
fun NavHostRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route,
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        composable(Screens.SplashScreen.route) {
            val viewModel: SplashScreenViewModel = hiltViewModel()
            val showTheProgressBar by viewModel.showTheProgressBar.collectAsStateWithLifecycle()
            SplashScreen(
                showTheProgressBar = showTheProgressBar,
                onGrantedThePermission = {
                    viewModel.loadImagesAndVideos(
                        onLoadComplete = {
                            navController.popBackStack().also {
                                navController.navigate(Screens.GalleryHome.route)
                            }
                        },
                        onError = {
                            Toast.makeText(
                                context,
                                "Error While Loading The Data",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        })
                })
        }
        composable(Screens.GalleryHome.route) {
            val viewModel: GalleryHomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            GalleryHomeScreen(folders = state) {
                navController.navigate(Screens.FolderDetails.route)
            }
        }
    }
}