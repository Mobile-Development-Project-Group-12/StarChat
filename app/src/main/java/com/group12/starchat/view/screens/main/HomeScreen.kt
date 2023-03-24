package com.group12.starchat.view.screens.main

import androidx.compose.runtime.Composable
import com.group12.starchat.viewModel.HomeUiState
import com.group12.starchat.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel?,
    onSignOutClick: () -> Unit
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()
}