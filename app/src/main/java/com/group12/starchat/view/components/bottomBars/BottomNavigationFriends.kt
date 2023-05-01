package com.group12.starchat.view.components.bottomBars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun BottomNavigationFriends(
    navToSettingsScreen: () -> Unit,
    navToProfileScreen: () -> Unit,
    navToHomeScreen: () -> Unit,
    navToSearchScreen: () -> Unit,
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        cutoutShape = MaterialTheme.shapes.large.copy(
            CornerSize(percent = 50)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(
                onClick = { navToSettingsScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Navigate to Settings Screen",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.alpha(0.3f)
                )
            }

            IconButton(
                onClick = { navToSearchScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Navigate to Search Screen",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.alpha(0.3f)
                )
            }

            IconButton(
                onClick = { navToHomeScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Navigate to Home Screen",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.alpha(0.3f)
                )
            }

            IconButton(
                onClick = { },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = "Navigate to Friends Screen",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                )
            }

            IconButton(
                onClick = { navToProfileScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Navigate to Profile Screen",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.alpha(0.3f)
                )
            }
        }
    }
}
