package com.group12.starchat.view.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.view.components.bottomBars.BottomNavigationSettings
import com.group12.starchat.view.components.topBars.SettingsTopBar
import com.group12.starchat.viewModel.SettingsUiState
import com.group12.starchat.viewModel.SettingsViewModel


@Composable
fun SettingsScreen(
    settingsViewmodel: SettingsViewModel?,
    onNavToProfilePage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onNavToFriendsPage: () -> Unit,
    onNavToSearchPage: () -> Unit,
) {

    val settingsUiState = settingsViewmodel?.settingsUiState ?: SettingsUiState()

    val themeEmoji = if (settingsUiState.darkMode) "🌙" else "☀️"

    Scaffold(
        topBar = { SettingsTopBar(currentScreen = "Settings") },
        bottomBar = { BottomNavigationSettings(navToProfileScreen = onNavToProfilePage, navToHomeScreen = onNavToHomePage, navToFriendsScreen = onNavToFriendsPage, navToSearchScreen = onNavToSearchPage) }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = MaterialTheme.colors.primary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        tint = MaterialTheme.colors.onSurface,
                        contentDescription = "Customization Icon",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "Customization",
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Divider(
                    color = MaterialTheme.colors.onSurface,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Dark Theme? $themeEmoji",
                        modifier = Modifier.alpha(0.7f),
                        color = MaterialTheme.colors.onSurface,
                    )
                    Switch(checked = settingsUiState.darkMode, onCheckedChange = { settingsViewmodel?.onDarkModeChange() })
                }
            }
        }
    }
}
