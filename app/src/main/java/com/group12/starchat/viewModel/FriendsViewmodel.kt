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

class FriendsViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    var friendsUiState by mutableStateOf(FriendsUiState())
    var blockedUiState by mutableStateOf(BlockedUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    private val _ListState = mutableStateOf(listState.NONE)
    val editListState: listState
        get() = _ListState.value

    fun onListChange(option: listState) {
        _ListState.value = option
    }

    fun loadfriends() {
        getFriends()
    }

    fun loadBlocked() {
        getBlocked()
    }

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

data class FriendsUiState(
    val friendsList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

data class BlockedUiState(
    val blockedList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

enum class listState{
    BLOCKED,
    FRIENDS,
    NONE
}
