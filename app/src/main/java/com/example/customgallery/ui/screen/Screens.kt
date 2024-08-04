package com.example.customgallery.ui.screen

sealed class Screens(val route: String) {
    data object SplashScreen : Screens(route = "splash_screen")
    data object GalleryHome : Screens(route = "gallery_home")
    data object FolderDetails : Screens(route = "folder_details")
    data object MediaDisplay : Screens(route = "media_display")
}