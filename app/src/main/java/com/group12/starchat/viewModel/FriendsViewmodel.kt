package com.group12.starchat.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

/**
 * This class is responsible for handling the logic for the Friends screen. It includes
 * methods such as loading the current users friends and blocked users.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class FriendsViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // an instance of the FriendsUiState class. Use this to access the values
    // within the ui state.
    var friendsUiState by mutableStateOf(FriendsUiState())

    // an instance of the BlockedUiState class. Use this to access the values
    // within the ui state.
    var blockedUiState by mutableStateOf(BlockedUiState())

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // This variable gets the current users id.
    private val userId: String
        get() = repository.getUserId()

    // This variable is used to determine which list of users to display.
    private val _ListState = mutableStateOf(listState.NONE)

    // This variable is used to get the value of the _ListState variable.
    val editListState: listState
        get() = _ListState.value

    /**
     * This method is used to change the state of the list of users. It does this by
     * replacing the current state with the new one in the ui state.
     *
     * @param option listState: This parameter is used to set the state of
     * the search bar. it can be Friends list or Blocked list.
     */
    fun onListChange(option: listState) {
        _ListState.value = option
    }

    /**
     * This method is used to load the current users friends list.
     **/
    fun loadfriends() {
        getFriends()
    }

    /**
     * This method is used to load the current users blocked list.
     **/
    fun loadBlocked() {
        getBlocked()
    }

    /**
     * This method is used to load the current users friends list. It does this by
     * finding the current users id, and returning the friends subcollection.
     **/
    private fun getFriends() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getFriends().collect {
                    friendsUiState = friendsUiState.copy(
                        friendsList = it
                    )
                }
            }
        } else {
            friendsUiState = friendsUiState.copy( friendsList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    /**
     * This method is used to load the current users blocked list. It does this by
     * finding the current users id, and returning the blocked subcollection.
     **/
    private fun getBlocked() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getBlocked().collect {
                    blockedUiState = blockedUiState.copy(
                        blockedList = it
                    )
                }
            }
        } else {
            blockedUiState = blockedUiState.copy( blockedList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

}

/**
 * This class is used to store the state of the Friends list.
 */
data class FriendsUiState(
    val friendsList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

/**
 * This class is used to store the state of the Blocked list.
 */
data class BlockedUiState(
    val blockedList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

/**
 * This enum class is used to store the what list the user is currently viewing.
 */
enum class listState{
    BLOCKED,
    FRIENDS,
    NONE
}
