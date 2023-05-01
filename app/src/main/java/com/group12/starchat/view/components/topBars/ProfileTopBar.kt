package com.group12.starchat.view.components.topBars

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileTopBar(
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp,
        modifier = Modifier
            .shadow(10.dp),
        title = {
            Text(
                text = currentScreen,
                style = TextStyle(
                    color = MaterialTheme.colors.primary,
                    fontSize = 20.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    )
}
