package com.group12.starchat.view.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.Resources
import com.group12.starchat.view.components.bottomBars.BottomNavigationSearch
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.components.topBars.SearchQueryTopBar
import com.group12.starchat.view.components.topBars.SearchScreenTopBar
import com.group12.starchat.viewModel.SearchBarState
import com.group12.starchat.viewModel.SearchUiState
import com.group12.starchat.viewModel.SearchViewModel
import com.group12.starchat.viewModel.SearchingState

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchForFriendsScreen(
    searchViewModel: SearchViewModel?,
    onNavToSettingsPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onNavToProfilePage: () -> Unit,
    onNavToFriendsPage: () -> Unit,
    onNavToUserProfile: (String) -> Unit,
) {
    val searchUiState = searchViewModel?.searchUiState ?: SearchUiState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        searchViewModel?.loadusers()
    }

    when (searchUiState.usersList) {
        is Resources.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        is Resources.Success -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                bottomBar = {
                    BottomNavigationSearch(
                        navToSettingsScreen = { onNavToSettingsPage() },
                        navToProfileScreen = { onNavToProfilePage() },
                        navToHomeScreen = { onNavToHomePage() },
                        navToFriendsScreen = { onNavToFriendsPage() },
                    )
                },
                topBar = {

                    when (searchViewModel?.searchState) {
                        SearchBarState.Open -> {
                            SearchQueryTopBar(
                                searchViewModel = searchViewModel,
                            )
                        }
                        SearchBarState.Closed -> {
                            SearchScreenTopBar(
                                currentScreen = "Search",
                                searchViewModel = searchViewModel,
                            )
                        }
                        else -> {
                            TopAppBar(
                                title = { Text(text = "Error has Occurred") }
                            )
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when (searchViewModel?.searchingState) {
                        SearchingState.Initial -> {
                            Text(
                                text = "Search for friends here!",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                color = MaterialTheme.colors.primary
                            )
                        }
                        SearchingState.Searching -> {
                            if (searchUiState.usersList.data?.isEmpty() == true) {
                                Text(
                                    text = "No users found",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(Alignment.Center)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    items(searchUiState.usersList.data!!) { user ->
                                        UsersItem(
                                            user = user,
                                            onNavToUserProfile = { userId ->
                                                onNavToUserProfile(userId)
                                            }
                                        )
                                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                                    }
                                }
                            }
                        } else -> {
                            Text(
                                text = "Error has Occurred",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
        is Resources.Failure -> {
            Text(
                text = "Error",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun UsersItem(
    user: User,
    onNavToUserProfile: (String) -> Unit,
) {

    rememberScaffoldState()
    
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                onClick = {
                    onNavToUserProfile(user.userId)
                }
            )
    ) {

        coilImage(
            url = user.imageUrl,
            modifier = Modifier.size(width = 50.dp, height = 50.dp),
            shape = RoundedCornerShape(100)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp)
        ) {
            Text(
                text = user.userName,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                fontStyle = MaterialTheme.typography.body1.fontStyle,
                color = MaterialTheme.colors.primary,
                fontSize = 16.sp,
            )
            Text(
                text = user.bio,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                fontStyle = MaterialTheme.typography.body1.fontStyle,
                color = MaterialTheme.colors.primary,
                fontSize = 16.sp,
            )
        }
    }
}