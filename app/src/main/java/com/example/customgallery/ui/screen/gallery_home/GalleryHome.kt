package com.example.customgallery.ui.screen.gallery_home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.customgallery.R
import com.example.customgallery.model.FolderWithMedia
import com.example.customgallery.model.MediaType
import com.example.customgallery.ui.screen.commen_component.ImageCard
import com.example.customgallery.ui.screen.commen_component.VideoCardWithThumb
import com.example.customgallery.ui.theme.CustomGalleryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryHomeScreen(
    modifier: Modifier = Modifier,
    folders: List<FolderWithMedia>,
    onClickFolder: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = "Gallery Home")
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp)) {
            items(items = folders, key = { it.folderName }) { folder ->
                FolderCard(folder = folder) { onClickFolder(folder.folderName) }
            }
        }
    }
}

@Composable
fun FolderCard(modifier: Modifier = Modifier, folder: FolderWithMedia, onClickFolder: () -> Unit) {
    Box(
        modifier = modifier
            .size(250.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClickFolder() }) {
        when (folder.mediaList.first().mediaType) {
            MediaType.IMAGE -> ImageCard(
                uri = folder.mediaList.first().uri,
                modifier = Modifier.align(Alignment.Center)
            )

            MediaType.VIDEO -> VideoCardWithThumb(
                uri = folder.mediaList.first().uri,
                modifier = Modifier.align(Alignment.Center)
            )

            MediaType.UNKNOWN -> Unit
        }
        FolderInformation(
            folderName = folder.folderName,
            imagesCount = folder.imagesCount,
            videosCount = folder.videosCount,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )

    }
}

@Composable
fun FolderInformation(
    modifier: Modifier = Modifier,
    folderName: String,
    imagesCount: Int,
    videosCount: Int
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (imagesCount > 0)
            MediaIconWithQuantity(mediaIcon = imagesCount, quantity = imagesCount)

        Text(text = folderName, fontWeight = FontWeight.Bold)
        if (videosCount > 0)
            MediaIconWithQuantity(mediaIcon = videosCount, quantity = videosCount)
    }
}

@Composable
fun MediaIconWithQuantity(modifier: Modifier = Modifier, mediaIcon: Int, quantity: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(mediaIcon),
            contentDescription = "Folder",
            Modifier.size(50.dp)
        )
        Text(text = quantity.toString())
    }
}

@Preview(showBackground = true)
@Composable
private fun GalleryHomeScreenPreview() {
    CustomGalleryTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            GalleryHomeScreen(folders = (1..15).map {
                FolderWithMedia(
                    folderName = it.toString(),
                    mediaList = emptyList(),
                    videosCount = 0,
                    imagesCount = 0
                )
            }, onClickFolder = {})
        }
    }
}