package com.group12.starchat.view.screens.main

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.group12.starchat.view.components.coilImages.coilImage2
import com.group12.starchat.view.components.topBars.GeneralTopBar
import com.group12.starchat.viewModel.ViewProfileUiState
import com.group12.starchat.viewModel.ViewProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ViewProfileScreen(
    viewProfileViewModel: ViewProfileViewModel?,
    navController: NavController,
    userId: String
) {
    val viewProfileUiState = viewProfileViewModel?.viewProfileUiState ?: ViewProfileUiState()

    val scaffoldState = rememberScaffoldState()

    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    val addOrRemoveText = if (viewProfileUiState.friends) "Remove Friend" else "Add Friend"

    val blockOrUnblockText = if (viewProfileUiState.blocked) "Unblock User" else "Block User"

    LaunchedEffect(key1 = Unit) {
        viewProfileViewModel?.loadUserProfile(userId)
        viewProfileViewModel?.loadCurrentUserFriends()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { GeneralTopBar(currentScreen = "User Profile", navController = navController) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 0.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    coilImage2(
                        url = viewProfileUiState.imageUrl,
                        modifier = Modifier,
                        shape = RectangleShape
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!viewProfileUiState.blocked) {
                                if (viewProfileUiState.friends) {
                                    viewProfileViewModel?.onFriendsChange(false)
                                }
                                viewProfileViewModel?.onBlockedChange(true)
                                viewProfileViewModel?.blockUser()
                                scope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar("You have blocked this user")
                                }
                            } else {
                                viewProfileViewModel?.onBlockedChange(false)
                                viewProfileViewModel?.unblockUser()
                                scope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar("You have unblocked this user!")
                                }
                            } },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        ),
                        shape = RoundedCornerShape(30),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = blockOrUnblockText,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    Button(
                        onClick = {
                            if (!viewProfileUiState.friends) {
                                if (viewProfileUiState.blocked) {
                                    viewProfileViewModel?.onBlockedChange(false)
                                }
                                viewProfileViewModel?.onFriendsChange(true)
                                viewProfileViewModel?.addFriend()
                                scope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar("Friend added!")
                                }
                            } else {
                                viewProfileViewModel?.onFriendsChange(false)
                                viewProfileViewModel?.removeFriend()
                                scope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar("You have removed this friend!")
                                }
                            } },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onSurface
                        ),
                        shape = RoundedCornerShape(30),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = addOrRemoveText,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }

                Column(
                    Modifier
                        .scrollable(
                            state = scrollState,
                            orientation = Orientation.Vertical,
                            flingBehavior = ScrollableDefaults.flingBehavior()
                        )
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(16.dp)
                    // .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                ) {
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "UserName Icon",
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .height(55.dp)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = viewProfileUiState.userName,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .fillMaxHeight(0.5f),
                                        color = MaterialTheme.colors.primary,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Profile UserName",
                                        color = MaterialTheme.colors.onSurface,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notes,
                                    contentDescription = "Profile Description Icon",
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .height(55.dp)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = viewProfileUiState.bio,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .fillMaxHeight(0.5f),
                                        color = MaterialTheme.colors.primary,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Profile Description",
                                        color = MaterialTheme.colors.onSurface,
                                        fontSize = 12.sp,
                                    )
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = "Email Icon",
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .height(55.dp)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = viewProfileUiState.email,
                                        color = MaterialTheme.colors.primary,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .fillMaxHeight(0.5f)
                                    )
                                    Text(
                                        text = "Profile Email Address",
                                        color = MaterialTheme.colors.onSurface,
                                        fontSize = 12.sp,
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}