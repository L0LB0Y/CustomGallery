package com.example.customgallery.data.repository

import com.example.customgallery.data.local.ImagesAndVideoLoader
import com.example.customgallery.model.FolderWithMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesAndVideoLoaderRepository @Inject constructor(private val imagesAndVideoLoader: ImagesAndVideoLoader) {
    private lateinit var allFolders: List<FolderWithMedia>

    suspend fun initializeTheData() = withContext(Dispatchers.IO) {
        allFolders = imagesAndVideoLoader.loadImagesAndVideos()
    }

    fun getAllFoldersNameWithThumbnails(): List<FolderWithMedia> {
        return allFolders
    }

    suspend fun getFolderByName(folderName: String): FolderWithMedia? =
        withContext(Dispatchers.Default) {
            allFolders.find { it.folderName == folderName }
        }
}