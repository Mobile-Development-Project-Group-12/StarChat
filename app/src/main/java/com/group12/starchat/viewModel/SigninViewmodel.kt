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

/**
 * This class is responsible for handling the logic for the SignIn screen. It
 * includes methods such as logging in a user, and validating the login form.
 *
 * @param repository AuthenticationRepo: This parameter is used to access methods
 * that interact with the Firebase's authentication system.
 * @author Daniel Mendes
 */
class SigninViewModel(
    private val repository: AuthenticationRepo = AuthenticationRepo(),
) : ViewModel() {

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // an instance of the LoginUiState class. Use this to access the values
    // within the ui state.
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    /**
     * This method is used to change the current users email. It does this by
     * replacing the current email with the new one in the ui state.
     *
     * @param Email String: This parameter is used to set the email value for the
     * current user.
     */
    fun onEmailChange(Email: String) {
        loginUiState = loginUiState.copy(email = Email)
    }

    /**
     * This method is used to change the current users password. It does this by
     * replacing the current password with the new one in the ui state.
     *
     * @param password String: This parameter is used to set the password value for
     * the current user.
     */
    fun onPasswordNameChange(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    /**
     * This method is used to validate the login form. It does this by checking if
     * the email and password fields are empty.
     *
     * @return Boolean: This method returns true if the email and password fields
     * are not empty, and false otherwise.
     */
    private fun validateLoginForm() =
        loginUiState.email.isNotBlank() &&
                loginUiState.password.isNotBlank()

    /**
     * This method is used to login a user. It does this by calling the login
     * method from the AuthenticationRepo class.
     *
     * @param context Context: This parameter is used to display a toast message
     * to the user.
     */
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

/**
 * This ui state class is used to hold the values that will be displayed on the
 * Login screen.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
)
