package com.example.customgallery.ui.screen

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.customgallery.ui.screen.folder_details.FolderDetailsScreen
import com.example.customgallery.ui.screen.folder_details.FolderDetailsViewModel
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
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
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
            GalleryHomeScreen(folders = state) { folderName ->
                navController.navigate("${Screens.FolderDetails.route}/$folderName")
            }
        }
        composable(
            route = "${Screens.FolderDetails.route}/{folder_name}",
            arguments = listOf(navArgument("folder_name") { type = NavType.StringType })
        ) {
            val viewModel: FolderDetailsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            state?.let {
                FolderDetailsScreen(
                    folderWithMedia = it,
                    exoPlayer = viewModel.exoPlayer,
                    onClickBack = { navController.popBackStack() },
                    onSelectedVideoToDisplayed = { uri ->
                        viewModel.exoPlayer.apply {
                            prepare()
                            setMediaItem(MediaItem.fromUri(uri))
                            playWhenReady = true
                            repeatMode = Player.REPEAT_MODE_ONE
                        }
                    })
            }
        }
    }
}