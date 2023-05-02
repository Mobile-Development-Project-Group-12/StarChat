package com.group12.starchat.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * This class is responsible for handling the logic for the Settings screen. It
 * includes methods such as changing the dark mode setting.
 *
 *  @author Daniel Mendes
 */
class SettingsViewModel: ViewModel() {

    // an instance of the SettingsUiState class. Use this to access the values
    var settingsUiState by mutableStateOf(SettingsUiState())
        private set

    /**
     * This method is used to change the dark mode setting. It does this by
     * changing the dark mode value in the ui state. This method is called from
     * MainActivity.kt, which displays a light or dark theme depending on this value,
     * before any composable functions are called.
     */
    fun onDarkModeChange() = viewModelScope.launch {
        settingsUiState = settingsUiState.copy(darkMode = !settingsUiState.darkMode)
    }

}

/**
 * This class is used to hold the values that will be displayed on the Settings
 * screen.
 */
data class SettingsUiState(
    val darkMode: Boolean = false,
)
