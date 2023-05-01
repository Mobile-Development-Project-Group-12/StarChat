package com.group12.starchat.view.components.topBars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun GeneralTopBar(
    currentScreen: String,
    navController: NavController
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp,
        modifier = Modifier
            .shadow(10.dp),
        actions = {
            Box(
                modifier = Modifier
                    .size(20.dp)
            ) {

            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    )
}
