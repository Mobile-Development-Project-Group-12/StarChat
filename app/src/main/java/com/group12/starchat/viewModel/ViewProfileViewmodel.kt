package com.group12.starchat.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

class ViewProfileViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    var viewProfileUiState by mutableStateOf(ViewProfileUiState())
        private set

    fun onFriendsChange(friends: Boolean) {
        viewProfileUiState = viewProfileUiState.copy(
            friends = friends
        )
    }

    fun onBlockedChange(blocked: Boolean) {
        viewProfileUiState = viewProfileUiState.copy(
            blocked = blocked
        )
    }

    fun loadUserProfile(userId: String) = viewModelScope.launch {
        repository.getUser(
            userId = userId,
            onSuccess = {
                viewProfileUiState = viewProfileUiState.copy(
                    userId = it!!.userId,
                    userName = it.userName,
                    imageUrl = it.imageUrl,
                    bio = it.bio,
                    email = it.email
                    )
            },
            onError = {
                Log.e("///// Current User Not Found /////", "Error loading profile: $it")
            }
        )
    }

    fun loadCurrentUserFriends() = viewModelScope.launch {
        repository.getFriends().collect {
            viewProfileUiState = viewProfileUiState.copy(
                currentUserFriends = it
            )
        }
    }

    /**
     * This method resets the entry added status to false. This viewmodel can then
     * be used again to add / view / change / delete another entry.
     */

    fun addFriend() = viewModelScope.launch {
        repository.addFriend(
            viewProfileUiState.userId,
            viewProfileUiState.userName,
            viewProfileUiState.imageUrl,
            viewProfileUiState.bio,
            viewProfileUiState.email
        )

    }

    fun removeFriend() = viewModelScope.launch {
        repository.removeFriend(
            viewProfileUiState.userId,
        )
    }

    fun blockUser() = viewModelScope.launch {
        repository.blockUser(
            viewProfileUiState.userId,
            viewProfileUiState.userName,
            viewProfileUiState.imageUrl,
            viewProfileUiState.bio,
            viewProfileUiState.email
        )
    }

    fun unblockUser() = viewModelScope.launch {
        repository.unBlockUser(
            viewProfileUiState.userId,
        )
    }

}

data class ViewProfileUiState(
    val userId: String = "",
    val userName: String = "",
    var imageUrl: String = "",
    val bio: String = "",
    val email: String = "",
    val currentUserFriends: Resources<List<User>> = Resources.Loading(),
    val friends: Boolean = false,
    val blocked: Boolean = false,
)