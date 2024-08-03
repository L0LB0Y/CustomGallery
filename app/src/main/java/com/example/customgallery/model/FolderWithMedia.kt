package com.example.customgallery.model

import androidx.compose.runtime.Immutable

@Immutable
data class FolderWithMedia(
    val folderName: String,
    val mediaList: List<DeviceMedia>,
    val videosCount: Int,
    val imagesCount: Int
)
