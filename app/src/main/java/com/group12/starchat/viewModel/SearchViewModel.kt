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

/**
 *  This class is responsible for handling the logic for the Search screen. It includes
 *  methods such as searching for users, and loading their bio, profile picture and
 *  username.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class SearchViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // an instance of the SearchUiState class. Use this to access the values
    // within the ui state.
    var searchUiState by mutableStateOf(SearchUiState())

    // an instance of the current user state class. Use this to access the values
    // of a certain user.
    var currentUserState by mutableStateOf(currentUserState())

    // This variable controls the state of the search bar. It can be open or closed.
    private val _searchState = mutableStateOf(SearchBarState.Closed)

    // Use this variable to access the value of the search state.
    val searchState: SearchBarState
        get() = _searchState.value

    // This variable controls the state of the searching state. It can be initial, or
    // searching. Initial allows you to display a message before searching, such as
    // "Search for a user".
    private val _searchingState = mutableStateOf(SearchingState.Initial)

    // Use this variable to access the value of the searching state.
    val searchingState: SearchingState
        get() = _searchingState.value

    // This variable controls the state of the search query. It can be blank, or
    // contain a string. This string is used to perform a query on the firestore
    // database.
    private val _searchQuery = mutableStateOf("")

    // Use this variable to access the value of the search query.
    val searchQuery: String
        get() = _searchQuery.value

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // This variable is used to get the current user's id.
    private val userId: String
        get() = repository.getUserId()

    /**
     * This method is used to change the state of the search bar. It does this by
     * replacing the current state with the new one in the ui state.
     *
     * @param option SearchBarState: This parameter is used to set the state of
     * the search bar. it can be SearchBarState.Open or SearchBarState.Closed.
     */
    fun onSearchBarChange(option: SearchBarState) {
        _searchState.value = option
    }

    /**
     * This method is used to change the state of the searching state. It does this by
     * replacing the current state with the new one in the ui state.
     *
     * @param option SearchingState: This parameter is used to set the state of
     * the searching state. it can be SearchingState.Initial or SearchingState.Searching.
     */
    fun onSearchingStateChange(option: SearchingState) {
        _searchingState.value = option
    }

    /**
     * This method is used to change the state of the search query. It does this by
     * replacing the current state with the new one in the ui state.
     *
     * @param query String: This parameter is used to set the state of
     * the search query.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * This method is used to load the users that have been searched for. It does
     * this by performing a query on the firestore database in order to get all
     * users that match the search query.
     */
    fun loadusers() {
        searchUiState.usersList = Resources.Success(listOf())
        loadProfile()
    }

    /**
     * This method is used to search for users by their username. It does this by
     * performing a query on the firestore database in order to get all
     * users that match the search query.
     */
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

    /**
     * This method is used to load the current user's profile. It does this by getting
     * the current user's id, and then calling the getUser method in DatabaseRepo.
     */
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
}

/**
 * This class is used to store the state of the search screen. It contains a list
 * of users, and a boolean that is used to check if a room has been created.
 */
data class SearchUiState(
    var usersList: Resources<List<User>> = Resources.Loading(),
    val roomCreated: Boolean = false,
)

/**
 * This class is used to store the state of the current user.
 */
data class currentUserState(
    val userId: String = "",
    val userName: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String = "",
    val bio: String = "",
    val email: String = "",
)

/**
 * This class is used to store the state of the search bar. The search bar can be
 * opened or closed.
 */
enum class SearchBarState {
    Open,
    Closed
}

/**
 * This class is used to store the state of the searching state. The searching state
 * can display an initial message (example: Start by Searching for a user!), or it
 * can display the results of the search.
 */
enum class SearchingState {
    Searching,
    Initial
}