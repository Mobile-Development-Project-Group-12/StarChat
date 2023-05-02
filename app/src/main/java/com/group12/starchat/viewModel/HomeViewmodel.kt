package com.group12.starchat.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

/**
 * This class is responsible for handling the logic for the Home screen. It
 * includes methods such as loading the current users rooms, and deleting rooms.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class HomeViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // an instance of the HomeUiState class. Use this to access the values
    // within the ui state.
    var homeUiState by mutableStateOf(HomeUiState())

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // This variable gets the current users id.
    private val userId: String
        get() = repository.getUserId()

    /**
     * This method is used to load the current users chat rooms.
     **/
    fun loadRooms() {
        getRooms()
    }

    /**
     * This method is used to load the current users chat rooms. It does this by
     * finding the current users id, and returning all of the rooms that the users
     * Id appears in.
     **/
    private fun getRooms() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getRooms().collect {
                    homeUiState = homeUiState.copy(
                        chatsList = it
                    )
                }
            }
        } else {
            homeUiState = homeUiState.copy( chatsList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    /**
     * This method is used to delete a room. It does this by calling the deleteRoom
     * method from the DatabaseRepo class.
     *
     * @param roomId String: This parameter is used to get the room from the
     * database that the user wants to delete.
     **/
    fun deleteRoom(roomId: String) {
        repository.deleteRoom(roomId = roomId) {
            homeUiState = homeUiState.copy(roomDeletedStatus = it)
        }
    }

    /**
     * This method signs the currently logged in user out of the app.
     **/
    fun signOut() = repository.signOut()

}

/**
 * This class is used to store the values that are used in the Home screen.
 */
data class HomeUiState(
    val chatsList: Resources<List<Rooms>> = Resources.Loading(),
    val roomCreated: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    var imageUrl: String = "",
    val bio: String = "",
    val online: Boolean = false,
    val roomDeletedStatus: Boolean = false,
)
