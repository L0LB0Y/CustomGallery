package com.example.customgallery.ui.screen.gallery_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customgallery.data.repository.ImagesAndVideoLoaderRepository
import com.example.customgallery.model.FolderWithMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryHomeViewModel @Inject constructor(
    private val imagesAndVideoLoaderRepository: ImagesAndVideoLoaderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<FolderWithMedia>>(emptyList())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            imagesAndVideoLoaderRepository.initializeTheData()
            _uiState.update { imagesAndVideoLoaderRepository.getAllFoldersNameWithThumbnails() }
        }
    }


}