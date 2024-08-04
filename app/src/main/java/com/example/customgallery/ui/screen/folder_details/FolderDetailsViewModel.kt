package com.example.customgallery.ui.screen.folder_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.customgallery.data.repository.ImagesAndVideoLoaderRepository
import com.example.customgallery.model.FolderWithMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imagesAndVideoLoaderRepository: ImagesAndVideoLoaderRepository,
    val exoPlayer: ExoPlayer
) : ViewModel() {
    private val folderName: String = checkNotNull(savedStateHandle["folder_name"])

    private val _uiState = MutableStateFlow<FolderWithMedia?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                imagesAndVideoLoaderRepository.getFolderByName(
                    folderName = folderName
                )
            }
        }
    }
}