package com.group12.starchat.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.group12.starchat.model.repository.AuthenticationRepo

class HomeViewModel(
    private val repository: AuthenticationRepo = AuthenticationRepo(),
): ViewModel() {

    // Home Screen Logic goes here
    // (Friends list Logic)
    var homeUiState by mutableStateOf(HomeUiState())

}

data class HomeUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
)