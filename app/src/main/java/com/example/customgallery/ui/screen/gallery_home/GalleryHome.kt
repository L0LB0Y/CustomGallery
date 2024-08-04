package com.example.customgallery.ui.screen.gallery_home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customgallery.R
import com.example.customgallery.model.DeviceMedia
import com.example.customgallery.model.FolderWithMedia
import com.example.customgallery.model.MediaType
import com.example.customgallery.ui.screen.commen_component.ImageCard
import com.example.customgallery.ui.screen.commen_component.VideoCardWithThumb
import com.example.customgallery.ui.theme.CustomGalleryTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryHomeScreen(
    modifier: Modifier = Modifier,
    folders: List<FolderWithMedia>,
    onClickFolder: (String) -> Unit
) {
    var folderLayout by remember { mutableStateOf(FoldersLayout.GRID) }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = "Gallery Home")
            }, actions = {
                IconButton(onClick = {
                    folderLayout =
                        if (folderLayout == FoldersLayout.GRID) FoldersLayout.LINEAR else FoldersLayout.GRID
                }) {
                    Icon(
                        painter = painterResource(R.drawable.sort_24px),
                        contentDescription = "Folder",
                        Modifier.size(35.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        SharedTransitionLayout {
            AnimatedContent(targetState = folderLayout, label = "") { currentLayout ->
                when (currentLayout) {
                    FoldersLayout.GRID -> GridFoldersLayout(
                        modifier = Modifier.weight(1f),
                        folders = folders,
                        transitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        onClickFolder = onClickFolder
                    )

                    FoldersLayout.LINEAR ->
                        LinearFoldersLayout(
                            modifier = Modifier.weight(1f),
                            folders = folders,
                            transitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@AnimatedContent,
                            onClickFolder = onClickFolder
                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GridFoldersLayout(
    modifier: Modifier = Modifier,
    folders: List<FolderWithMedia>,
    transitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClickFolder: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(items = folders, key = { it.folderName }) { folder ->
            with(transitionScope) {
                FolderCard(
                    folder = folder, modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = folder.folderName + folder.mediaList.size),
                        animatedVisibilityScope = animatedContentScope

                    )
                ) { onClickFolder(folder.folderName) }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LinearFoldersLayout(
    modifier: Modifier = Modifier,
    folders: List<FolderWithMedia>,
    transitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClickFolder: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(items = folders, key = { it.folderName }) { folder ->
            with(transitionScope) {
                FolderCard(
                    folder = folder, modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = folder.folderName + folder.mediaList.size),
                        animatedVisibilityScope = animatedContentScope
                    )
                ) { onClickFolder(folder.folderName) }
            }
        }
    }

}

@Composable
fun FolderCard(modifier: Modifier = Modifier, folder: FolderWithMedia, onClickFolder: () -> Unit) {
    Column(modifier = Modifier) {
        Text(
            text = folder.folderName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = modifier
                .size(180.dp)
                .clickable { onClickFolder() }) {
            when (folder.mediaList.first().mediaType) {
                MediaType.IMAGE -> ImageCard(
                    uri = folder.mediaList.first().uri,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                )

                MediaType.VIDEO -> VideoCardWithThumb(
                    uri = folder.mediaList.first().uri,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                )

                MediaType.UNKNOWN -> Unit
            }
            FolderInformation(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                imagesCount = folder.imagesCount,
                videosCount = folder.videosCount
            )
        }

    }
}

@Composable
fun FolderInformation(
    modifier: Modifier = Modifier,
    imagesCount: Int,
    videosCount: Int
) {
    Box(modifier,
    ) {
        if (imagesCount > 0)
            MediaIconWithQuantity(
                mediaIconResourceID = R.drawable.image_24px,
                quantity = imagesCount,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        if (videosCount > 0)
            MediaIconWithQuantity(
                mediaIconResourceID = R.drawable.videocam_24px,
                quantity = videosCount,
                modifier = Modifier.align(Alignment.TopStart)
            )
    }
}

@Composable
fun MediaIconWithQuantity(modifier: Modifier = Modifier, mediaIconResourceID: Int, quantity: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(mediaIconResourceID),
            contentDescription = "Folder",
            Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 11.sp
        )
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
                    mediaList = (1..15).map {data->
                        DeviceMedia(
                            id = data.toLong(),
                            uri = "",
                            mediaType = MediaType.IMAGE
                        )
                    },
                    videosCount = 10,
                    imagesCount = 200
                )
            }, onClickFolder = {})
        }
    }
}