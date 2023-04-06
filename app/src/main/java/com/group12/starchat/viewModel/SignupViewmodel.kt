package com.group12.starchat.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.repository.AuthenticationRepo
import com.group12.starchat.model.repository.DatabaseRepo
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: AuthenticationRepo = AuthenticationRepo(),
    private val databaseRepository: DatabaseRepo = DatabaseRepo(),
) : ViewModel() {

    val hasUser: Boolean
        get() = repository.hasUser()

    var signupUiState by mutableStateOf(SignupUiState())
        private set

    fun onEmailChange(email: String) {
        signupUiState = signupUiState.copy(email = email)
    }

    fun onUserNameChange(userName: String) {
        signupUiState = signupUiState.copy(userName = userName)
    }

    fun onPasswordChangeSignup(password: String) {
        signupUiState = signupUiState.copy(password = password)
    }

    fun onConfirmPasswordChange(password: String) {
        signupUiState = signupUiState.copy(confirmPassword = password)
    }

    fun onImageChange(imageUri: Uri?){
        signupUiState = signupUiState.copy(imageUri = imageUri)
    }

    private fun validateSignupForm() =
        signupUiState.userName.isNotBlank() &&
                signupUiState.password.isNotBlank() &&
                signupUiState.email.isNotBlank() &&
                signupUiState.imageUri != null

    fun createUser(context: Context) = viewModelScope.launch {
        try {

            if (!validateSignupForm()) {
                throw IllegalArgumentException("email and password can not be empty")
            }

            signupUiState = signupUiState.copy(isLoading = true)

            if (signupUiState.password !=
                signupUiState.confirmPassword
            ) {
                throw IllegalArgumentException(
                    "Passwords do not match"
                )
            }

            signupUiState = signupUiState.copy(signUpError = null)

            repository.createUser(
                signupUiState.email,
                signupUiState.password
            ) { isSuccessful ->

                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "User Created",
                        Toast.LENGTH_SHORT
                    ).show()
                    signupUiState = signupUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "Failed to create user",
                        Toast.LENGTH_SHORT
                    ).show()
                    signupUiState = signupUiState.copy(isSuccessLogin = false)
                }

            }

        } catch (e: Exception) {
            signupUiState = signupUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            signupUiState = signupUiState.copy(isLoading = false)
        }

    }
}

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val imageUri: Uri? = null,
    val userName: String = "",
    val confirmPassword: String = "",

    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
)