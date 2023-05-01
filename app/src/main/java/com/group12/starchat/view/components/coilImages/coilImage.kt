package com.group12.starchat.view.components.coilImages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.group12.starchat.R

@Composable
fun coilImage(url: String, modifier: Modifier, shape: Shape) {

    val painter = rememberAsyncImagePainter(
        model = url,
        imageLoader = ImageLoader.Builder(LocalContext.current).crossfade(true)
            .placeholder(R.drawable.ic_launcher_background).crossfade(300).build()
    )

    /*
    Card(
        shape = shape,
        modifier = modifier,
        backgroundColor = androidx.compose.ui.graphics.Color.Transparent
    ) {

    }

     */

    Image(
        painter = painter,
        contentDescription = "ImagePainter",
        contentScale = ContentScale.None,
        alignment = Alignment.Center,
        modifier = modifier
            //.aspectRatio(1f)
            .fillMaxSize()
            .clip(shape)
    )

}

@Composable
fun coilImage(uri: Uri?, modifier: Modifier, shape: Shape) {

    /*
    Card(
        shape = shape,
        modifier = modifier
    ) {

    }

     */
    AsyncImage(
        model = uri,
        contentDescription = "ImagePainter",
        contentScale = ContentScale.None,
        alignment = Alignment.Center,
        modifier = modifier
            //.aspectRatio(1f)
            .fillMaxSize()
            .clip(shape)
    )
}

@Composable
fun coilImage2(url: String, modifier: Modifier, shape: Shape) {

    val painter = rememberAsyncImagePainter(
        model = url,
        imageLoader = ImageLoader.Builder(LocalContext.current).crossfade(true)
            .placeholder(R.drawable.ic_launcher_background).crossfade(300).build()
    )

    /*
    Card(
        shape = shape,
        modifier = modifier,
        backgroundColor = androidx.compose.ui.graphics.Color.Transparent
    ) {

    }

     */

    Image(
        painter = painter,
        contentDescription = "ImagePainter",
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier
            //.aspectRatio(1f)
            .fillMaxSize()
            .clip(shape)
    )

}

@Composable
fun coilImage2(uri: Uri?, modifier: Modifier, shape: Shape) {

    /*
    Card(
        shape = shape,
        modifier = modifier
    ) {

    }

     */
    AsyncImage(
        model = uri,
        contentDescription = "ImagePainter",
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier
            //.aspectRatio(1f)
            .fillMaxSize()
            .clip(shape)
    )
}
