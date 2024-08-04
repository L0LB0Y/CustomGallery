package com.example.customgallery.ui.screen

sealed class Screens(val route: String) {
    data object GalleryHome : Screens(route = "gallery_home")
    data object FolderWithMedia : Screens(route = "folder_with_media")
    data object MediaDetails : Screens(route = "media_details")
}