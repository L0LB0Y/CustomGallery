package com.example.customgallery.model

import com.example.customgallery.utils.MediaType

data class DeviceMedia(
    val id: Long,
    val uri: String,
    val mediaType: MediaType,
)
