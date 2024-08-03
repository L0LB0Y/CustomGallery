package com.example.customgallery.data.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.customgallery.model.DeviceMedia
import com.example.customgallery.model.FolderWithMedia
import com.example.customgallery.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesAndVideoLoader @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun loadImagesAndVideos(): List<FolderWithMedia> = withContext(Dispatchers.IO) {
        val contentResolver: ContentResolver = context.contentResolver
        val folders = mutableMapOf<String, MutableList<DeviceMedia>>()
        val allImages = mutableListOf<DeviceMedia>()
        val allVideos = mutableListOf<DeviceMedia>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val selection = (
                "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                )

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val uri = MediaStore.Files.getContentUri("external")

        val cursor: Cursor? = contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val bucketColumn =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val type = it.getInt(typeColumn)
                val bucketName = it.getString(bucketColumn) ?: "Unknown"

                val mediaType = when (type) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.IMAGE
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.VIDEO
                    else -> MediaType.UNKNOWN
                }

                val contentUri: Uri = when (mediaType) {
                    MediaType.IMAGE -> Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )

                    MediaType.VIDEO -> Uri.withAppendedPath(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )

                    MediaType.UNKNOWN -> continue
                }

                val mediaItem =
                    DeviceMedia(id = id, uri = contentUri.toString(), mediaType = mediaType)
                val mediaList = folders.getOrPut(bucketName) { mutableListOf() }
                mediaList.add(mediaItem)

                // Add to all images or all videos folder
                when (mediaType) {
                    MediaType.IMAGE -> allImages.add(mediaItem)
                    MediaType.VIDEO -> allVideos.add(mediaItem)
                    else -> {}
                }
            }
        }

        // Add "All Images" and "All Videos" folders
        if (allImages.isNotEmpty()) {
            folders["All Images"] = allImages
        }
        if (allVideos.isNotEmpty()) {
            folders["All Videos"] = allVideos
        }

        // Sort folders alphabetically by name
        folders.toSortedMap().map {
            FolderWithMedia(
                folderName = it.key,
                mediaList = it.value,
                videosCount = it.value.count { item -> item.mediaType == MediaType.VIDEO },
                imagesCount = it.value.count { item -> item.mediaType == MediaType.IMAGE })
        }
    }
}