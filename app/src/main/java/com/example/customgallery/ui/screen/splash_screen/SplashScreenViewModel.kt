package com.example.customgallery.ui.screen.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customgallery.data.repository.ImagesAndVideoLoaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val imagesAndVideoLoaderRepository: ImagesAndVideoLoaderRepository
) : ViewModel() {
    private val _showTheProgressBar = MutableStateFlow(false)
    val showTheProgressBar = _showTheProgressBar.asStateFlow()
    fun loadImagesAndVideos(onLoadComplete: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            _showTheProgressBar.update { true }
            try {
                imagesAndVideoLoaderRepository.initializeTheData()
                onLoadComplete()
            } catch (ex: Exception) {
                onError()
            }
            _showTheProgressBar.update { false }
        }
    }

}