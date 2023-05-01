package com.group12.starchat.viewModel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.repository.AuthenticationRepo
import kotlinx.coroutines.launch

class SigninViewModel(
    private val repository: AuthenticationRepo = AuthenticationRepo(),
) : ViewModel() {

    val hasUser: Boolean
        get() = repository.hasUser()

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(Email: String) {
        loginUiState = loginUiState.copy(email = Email)
    }

    fun onPasswordNameChange(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    /**
     * Sign up Profile
     */

    private fun validateLoginForm() =
        loginUiState.email.isNotBlank() &&
                loginUiState.password.isNotBlank()

    fun loginUser(context: Context) = viewModelScope.launch {
        try {

            if (!validateLoginForm()) {
                throw IllegalArgumentException("email and password can not be empty")
            }

            loginUiState = loginUiState.copy(isLoading = true)

            loginUiState = loginUiState.copy(loginError = null)
            repository.login(
                loginUiState.email,
                loginUiState.password
            ) { isSuccessful ->

                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }

            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
)