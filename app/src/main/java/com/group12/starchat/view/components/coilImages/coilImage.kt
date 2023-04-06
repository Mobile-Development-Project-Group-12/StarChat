package com.group12.starchat.view.components.coilImages

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Card(
        border = BorderStroke(width = 2.dp, Color.Green),
        shape = shape
    ) {
        Card(
            shape = shape,
            modifier = modifier.padding(4.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "ImagePainter",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Composable
fun coilImage(uri: Uri?, modifier: Modifier, shape: Shape) {

    Card(
        border = BorderStroke(width = 2.dp, Color.Green),
        shape = shape
    ) {
        Card(
            shape = shape,
            modifier = modifier.padding(4.dp)
        ) {
            AsyncImage(
                model = uri,
                contentDescription = "ImagePainter",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

