package com.example.customgallery.ui.screen.folder_details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.customgallery.R
import com.example.customgallery.model.DeviceMedia
import com.example.customgallery.model.FolderWithMedia
import com.example.customgallery.ui.screen.commen_component.ImageCard
import com.example.customgallery.ui.screen.commen_component.VideoCardWithThumb
import com.example.customgallery.utils.FoldersLayout
import com.example.customgallery.utils.MediaType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FolderDetailsScreen(
    modifier: Modifier = Modifier,
    folderWithMedia: FolderWithMedia,
    exoPlayer: ExoPlayer? = null,
    onClickBack: () -> Unit,
    onSelectedVideoToDisplayed: (String) -> Unit
) {
    var folderLayout by remember { mutableStateOf(FoldersLayout.GRID) }
    var showFolderInformation by remember { mutableStateOf(false) }
    var showMediaDisplay by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedMediaToDisplayed: DeviceMedia? by remember { mutableStateOf(null) }
    val state = rememberLazyGridState()
    BackHandler {
        if (showMediaDisplay)
            showMediaDisplay = false
        else
            onClickBack()
    }
    Box(modifier = modifier) {
        SharedTransitionLayout {
            AnimatedContent(targetState = showMediaDisplay, label = "") { currentState ->
                when (currentState) {
                    true -> selectedMediaToDisplayed?.let {
                        MediaDisplayedLayout(
                            exoPlayer = exoPlayer!!,
                            media = it,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@AnimatedContent,
                            onClickBack = { showMediaDisplay = false }
                        )
                    }

                    false -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            FolderDetailsTopBar(
                                folderName = folderWithMedia.folderName,
                                onClickChangeLayout = {
                                    folderLayout =
                                        if (folderLayout == FoldersLayout.GRID) FoldersLayout.LINEAR else FoldersLayout.GRID
                                },
                                onClickShowFolderInformation = {
                                    showFolderInformation = true
                                }, onClickBack = onClickBack
                            )
                            FolderDetailsMediaLayout(
                                numberOfColumns = if (folderLayout == FoldersLayout.GRID) 2 else 1,
                                onClickMedia = {
                                    if (it.mediaType == MediaType.VIDEO)
                                        onSelectedVideoToDisplayed(it.uri)
                                    selectedMediaToDisplayed = it
                                    showMediaDisplay = true
                                },
                                mediaList = folderWithMedia.mediaList,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this@AnimatedContent,
                                state = state
                            )

                        }
                    }
                }
            }
        }

        if (showFolderInformation)
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showFolderInformation = false }) {
                FolderInformation(
                    imagesCount = folderWithMedia.imagesCount,
                    videosCount = folderWithMedia.videosCount
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailsTopBar(
    folderName: String,
    onClickChangeLayout: () -> Unit,
    onClickShowFolderInformation: () -> Unit,
    onClickBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    painter = painterResource(R.drawable.reply_24px),
                    contentDescription = "Folder",
                    Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(text = folderName)
        }, actions = {
            IconButton(onClick = onClickChangeLayout) {
                Icon(
                    painter = painterResource(R.drawable.sort_24px),
                    contentDescription = "Folder",
                    Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onClickShowFolderInformation) {
                Icon(
                    painter = painterResource(R.drawable.info_24px),
                    contentDescription = "Folder",
                    Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun FolderInformation(modifier: Modifier = Modifier, imagesCount: Int, videosCount: Int) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "This Folder Contains Images: $imagesCount",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "This Folder Contains Videos: $videosCount",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FolderDetailsMediaLayout(
    modifier: Modifier = Modifier,
    numberOfColumns: Int,
    mediaList: List<DeviceMedia>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    state: LazyGridState,
    onClickMedia: (DeviceMedia) -> Unit
) {
    LazyVerticalGrid(
        state = state,
        modifier = modifier,
        columns = GridCells.Fixed(numberOfColumns),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(items = mediaList, key = { it.id }) { media ->
            with(sharedTransitionScope) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onClickMedia(media) }) {
                    when (media.mediaType) {
                        MediaType.IMAGE -> ImageCard(
                            uri = media.uri,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = media.id),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .align(Alignment.Center)
                                .fillMaxSize()
                        )

                        MediaType.VIDEO -> VideoCardWithThumb(
                            uri = media.uri,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = media.id),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .align(Alignment.Center)
                                .fillMaxSize()
                        )

                        MediaType.UNKNOWN -> Unit
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaDisplayedLayout(
    modifier: Modifier = Modifier,
    media: DeviceMedia,
    exoPlayer: ExoPlayer,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClickBack: () -> Unit
) {
    Column {
        TopAppBar(navigationIcon = {
            IconButton(onClickBack) {
                Icon(
                    painter = painterResource(R.drawable.reply_24px),
                    contentDescription = "Folder",
                    Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }, title = {})

        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            with(sharedTransitionScope) {
                when (media.mediaType) {
                    MediaType.IMAGE -> ImageCard(
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = media.id),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .size(500.dp),
                        uri = media.uri
                    )

                    MediaType.VIDEO -> VidePlayer(
                        exoPlayer = exoPlayer, modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = media.id),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .size(500.dp)
                    )

                    MediaType.UNKNOWN -> Unit
                }
            }
        }
    }
}

@Composable
fun VidePlayer(modifier: Modifier = Modifier, exoPlayer: ExoPlayer) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
        }
    }
    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    )
}
