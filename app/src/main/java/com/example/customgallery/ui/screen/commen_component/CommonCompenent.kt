package com.example.customgallery.ui.screen.commen_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import coil.request.videoFramePercent
import com.example.customgallery.R
import com.example.customgallery.ui.theme.CustomGalleryTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun ImageCard(modifier: Modifier = Modifier, uri: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.background_cover),
        contentDescription = uri,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun VideoCardWithThumb(modifier: Modifier = Modifier, uri: String) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .decoderFactory(VideoFrameDecoder.Factory())
                .videoFrameMillis(1000)
                .decoderDispatcher(Dispatchers.IO)
                .build(),
            placeholder = painterResource(R.drawable.background_cover),
            contentDescription = "stringResource(R.string.description)",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .clip(MaterialTheme.shapes.small)
        )
        Icon(
            painter = painterResource(id = R.drawable.play_circle_24px),
            contentDescription = "Play",
            modifier = Modifier
                .align(Alignment.Center)
                .size(50.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
private fun VideoCardWithThumbPreview() {
    CustomGalleryTheme {
        VideoCardWithThumb(uri = "")
    }
}

@Preview
@Composable
private fun ImageCardPreview() {
    CustomGalleryTheme {
    ImageCard(uri = "")
    }
}