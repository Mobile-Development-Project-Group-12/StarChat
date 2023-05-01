package com.group12.starchat.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.repository.DatabaseRepo
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    /** This variable gets the current state of the Entry Ui, aswell as sets the state of the Entry Ui*/
    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    private val _UserNameState = mutableStateOf(UserNameState.Viewing)
    val editUserNameState: UserNameState
        get() = _UserNameState.value

    private val _BioState = mutableStateOf(BioState.Viewing)
    val editBioState: BioState
        get() = _BioState.value

    /** This private variable checks if there is a user logged in */
    private val hasUser:Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun onUserNameChange(userName: String){
        profileUiState = profileUiState.copy(userName = userName)
    }

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
    Editing,
    Viewing
}

enum class BioState {
    Editing,
    Viewing
}