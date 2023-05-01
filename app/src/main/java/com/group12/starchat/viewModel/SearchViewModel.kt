package com.group12.starchat.viewModel

import android.net.Uri
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

class SearchViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    var searchUiState by mutableStateOf(SearchUiState())
    var currentUserState by mutableStateOf(currentUserState())

    private val _searchState = mutableStateOf(SearchBarState.Closed)
    val searchState: SearchBarState
        get() = _searchState.value

    private val _searchingState = mutableStateOf(SearchingState.Initial)
    val searchingState: SearchingState
        get() = _searchingState.value

    private val _searchQuery = mutableStateOf("")
    val searchQuery: String
        get() = _searchQuery.value

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun onSearchBarChange(option: SearchBarState) {
        _searchState.value = option
    }

    fun onSearchingStateChange(option: SearchingState) {
        _searchingState.value = option
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadusers() {
        searchUiState.usersList = Resources.Success(listOf())
        loadProfile()
    }

    private fun getAllUsers() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getAllUsers().collect {
                    searchUiState = searchUiState.copy(
                        usersList = it
                    )
                }
            }
        } else {
            searchUiState = searchUiState.copy( usersList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    fun searchUsersByName() = viewModelScope.launch {
        repository.getUsersByName(
            userName = currentUserState.userName,
            query = searchQuery
        ).collect {
            searchUiState = searchUiState.copy(
                usersList = it
            )
        }
    }

    fun loadProfile() = viewModelScope.launch {
        if(hasUser){
            repository.getUser(
                userId = userId,
                onSuccess = {
                    currentUserState = currentUserState.copy(
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
     * This method signs the currently logged in user out of the app.
     **/
    fun signOut() = repository.signOut()

}

data class SearchUiState(
    var usersList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

data class currentUserState(
    val userId: String = "",
    val userName: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String = "",
    val bio: String = "",
    val email: String = "",
)

enum class SearchBarState {
    Open,
    Closed
}

enum class SearchingState {
    Searching,
    Initial
}