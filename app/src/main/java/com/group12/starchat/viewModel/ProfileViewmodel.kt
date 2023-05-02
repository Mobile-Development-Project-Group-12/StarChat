package com.group12.starchat.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.repository.DatabaseRepo
import kotlinx.coroutines.launch

/**
 * This class is responsible for handling the logic for the Profile screen. It
 * includes methods such as changing the user's profile picture, username, and bio.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class ProfileViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // an instance of the ProfileUiState class. Use this to access the values
    // within the ui state.
    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    // This private variable is used to check if the user is currently editing
    // their profile picture.
    private val _UserNameState = mutableStateOf(UserNameState.Viewing)

    // use this to access the value of the _UserNameState variable.
    val editUserNameState: UserNameState
        get() = _UserNameState.value

    // This private variable is used to check if the user is currently editing
    private val _BioState = mutableStateOf(BioState.Viewing)

    // use this to access the value of the _BioState variable.
    val editBioState: BioState
        get() = _BioState.value

    // This private variable is used to check if the user is currently editing
    private val hasUser:Boolean
        get() = repository.hasUser()

    // This variable is used to get the current user's id.
    private val userId: String
        get() = repository.getUserId()

    /**
     * This method is used to change the current users name. It does this by
     * replacing the current user name variable with the new one in the ui state.
     *
     * @param userName String: This parameter is used to set the username value
     * for the current user.
     */
    fun onUserNameChange(userName: String){
        profileUiState = profileUiState.copy(userName = userName)
    }

    /**
     * This method is used to change the current users bio. It does this by
     * replacing the current bio variable with the new one in the ui state.
     *
     * @param bio String: This parameter is used to set the bio value for the
     * current user.
     */
    fun onBioChange(bio: String){
        profileUiState = profileUiState.copy(bio = bio)
    }

    /**
     * This method takes in a file path from the system, and passes it to the Ui
     * State. The file should be an image file
     *
     * @param imageUri Uri? - This is the new image for the entry
     */
    fun onImageChange(imageUri: Uri?){
        profileUiState = profileUiState.copy(imageUri = imageUri)
    }

    /**
     * This method takes in an entry image (usually a firebase url), and passes it to
     * the UiState
     *
     * @param imageUrl String - This is the new image for the diary. This image
     * comes from a url on the internet, and is used as a default image incase
     * no image is selected from the users device.
     */
    fun onImageChangeUrl(imageUrl: String) {
        profileUiState = profileUiState.copy(imageUrl = imageUrl)
    }

    /**
     * This method is used to update the current user's profile. It does this by
     * calling the updateProfile method from the DatabaseRepo class.
     */
    fun updateProfile(){
        if(hasUser){
            repository.updateProfile(
                userName = profileUiState.userName,
                imageUri = profileUiState.imageUri,
                imageUrl = profileUiState.imageUrl,
                bio = profileUiState.bio,
            ) {
                profileUiState = profileUiState.copy(updatedProfileStatus = it)
            }
        }
    }

    /**
     * This method is used to load the current user's profile. It does this by
     * calling the getUser method from the DatabaseRepo class.
     */
    fun loadProfile() = viewModelScope.launch {
        if(hasUser){
            repository.getUser(
                userId = userId,
                onSuccess = {
                    profileUiState = profileUiState.copy(
                        userId = it!!.userId,
                        userName = it.userName,
                        imageUrl = it.imageUrl,
                        email = it.email,
                        bio = it.bio,
                    )
                },
                onError = {
                    Log.e("///// Current User Not Found /////", "Error loading profile: $it")
                }
            )
        }
    }

    /**
     * This method resets the entry added status to false. This viewmodel can then
     * be used again to add / view / change / delete another entry.
     */
    fun resetProfileUpdatedStatus(){
        profileUiState = profileUiState.copy(
            updatedProfileStatus = false
        )
    }

}

/**
 * This class is used to store the current user's profile information. It is
 * used to update the user's profile.
 */
data class ProfileUiState(
    val userId: String = "",
    val userName: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String = "",
    val bio: String = "",
    val email: String = "",
    val roomCreated: Boolean = false,
    val updatedProfileStatus: Boolean = false,
)

enum class UserNameState {
    // Editing,
    Viewing
}

enum class BioState {
    // Editing,
    Viewing
}