package com.example.customgallery.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.customgallery.ui.screen.gallery_home.GalleryHomeScreen
import com.example.customgallery.ui.screen.gallery_home.GalleryHomeViewModel

@Composable
fun NavHostRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.GalleryHome.route,
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        composable(Screens.GalleryHome.route) {
            val viewModel: GalleryHomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            GalleryHomeScreen(folders = state) {
                navController.navigate(Screens.FolderWithMedia.route)
            }
        }
    }
}