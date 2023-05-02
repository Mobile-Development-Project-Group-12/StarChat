package com.group12.starchat.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.repository.AuthenticationRepo
import com.group12.starchat.model.repository.DatabaseRepo
import kotlinx.coroutines.launch

/**
 *  This class is responsible for handling the logic for the SignUp screen. It
 *  includes methods such as creating a user, and validating the signup form.
 *
 *  @param repository AuthenticationRepo: This parameter is used to access methods
 *  that interact with the Firebase's authentication system.
 *  @param databaseRepository DatabaseRepo: This parameter is used to access
 *  methods that interact with the Firebase's Firestore database.
 *  @author Daniel Mendes
 */
class SignupViewModel(
    private val repository: AuthenticationRepo = AuthenticationRepo(),
    private val databaseRepository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // an instance of the SignupUiState class. Use this to access the values
    // within the ui state.
    var signupUiState by mutableStateOf(SignupUiState())
        private set

    /**
     * This method is used to change the current users email. It does this by
     * replacing the current email with the new one in the ui state.
     *
     * @param email String: This parameter is used to set the email value for the
     * current user.
     */
    fun onEmailChange(email: String) {
        signupUiState = signupUiState.copy(email = email)
    }

    /**
     * This method is used to change the current users username. It does this by
     * replacing the current username with the new one in the ui state.
     *
     * @param userName String: This parameter is used to set the username value for
     * the current user.
     */
    fun onUserNameChange(userName: String) {
        signupUiState = signupUiState.copy(userName = userName)
    }

    /**
     * This method is used to change the current users password. It does this by
     * replacing the current password with the new one in the ui state.
     *
     * @param password String: This parameter is used to set the password value for
     * the current user.
     */
    fun onPasswordChangeSignup(password: String) {
        signupUiState = signupUiState.copy(password = password)
    }

    /**
     * This method is used to change the on confirm password field. It does this by
     * replacing the current confirm password with the new one in the ui state.
     *
     * @param password String: This parameter is used to set the on Confirm
     * password value for the current user.
     */
    fun onConfirmPasswordChange(password: String) {
        signupUiState = signupUiState.copy(confirmPassword = password)
    }

    /**
     * This method is used to change the current users image. It does this by
     * replacing the current image with the new one in the ui state.
     *
     * @param imageUri Uri: This parameter is used to set the image value for the
     * current user. Uri is the path of the image in the device. The diffrence
     * between Uri and Urls in this project, is that Urls refer to paths on the
     * internet.
     */
    fun onImageChange(imageUri: Uri?){
        signupUiState = signupUiState.copy(imageUri = imageUri)
    }

    /**
     * This method is used to validate the signup form. It does this by checking
     * if all the fields are filled in, and correct. For example, if you dont add a
     * profile picture, the method will return false.
     *
     * @return Boolean: This method returns true if all the fields are filled in,
     * and false if not.
     */
    private fun validateSignupForm() =
        signupUiState.userName.isNotBlank() &&
                signupUiState.password.isNotBlank() &&
                signupUiState.email.isNotBlank() &&
                signupUiState.imageUri != null

    /**
     * This method is used to create a user. It does this by calling the createUser
     * method from the AuthenticationRepo class. It also calls the addUser method
     * from the DatabaseRepo class to add the user to the database.
     *
     * @param context Context: This parameter is used to display a toast message
     * on the current screen.
     */
    fun createUser(context: Context) = viewModelScope.launch {
        try {

            if (signupUiState.imageUri == null) {
                throw IllegalArgumentException("Add a profile picture!")
            }

            if (!validateSignupForm()) {
                throw IllegalArgumentException("Please fill all the fields!")
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

            databaseRepository.addUser(
                userName = signupUiState.userName,
                imageUri = signupUiState.imageUri ?: Uri.EMPTY,
                email = signupUiState.email
            ) {
                Log.e("///// User Added /////", "User Added Successfully to the database")
            }

        } catch (e: Exception) {
            signupUiState = signupUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            signupUiState = signupUiState.copy(isLoading = false)
        }

    }
}

/**
 * This ui state class is used to hold the values that will be displayed on the
 * SignUp screen.
 */
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