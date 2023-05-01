package com.group12.starchat.view.screens.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.view.components.bottomBars.BottomNavigationProfile
import com.group12.starchat.view.components.coilImages.coilImage2
import com.group12.starchat.view.components.topBars.ProfileTopBar
import com.group12.starchat.viewModel.BioState
import com.group12.starchat.viewModel.ProfileUiState
import com.group12.starchat.viewModel.ProfileViewModel
import com.group12.starchat.viewModel.UserNameState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profileViewmodel: ProfileViewModel?,
    onNavToSettingsPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onNavToFriendsPage: () -> Unit,
    onNavToSearchPage: () -> Unit,
) {

    val profileUiState = profileViewmodel?.profileUiState ?: ProfileUiState()

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()

    if (pickedPhoto != null) {
        profileViewmodel?.onImageChange(pickedPhoto)
    } else {
        profileViewmodel?.onImageChange(null)
    }

    val userNameEditIcon = if (profileViewmodel?.editUserNameState == UserNameState.Viewing) {
        Icons.Filled.Edit
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val bioEditIcon = if (profileViewmodel?.editBioState == BioState.Viewing) {
        Icons.Filled.Edit
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        profileViewmodel?.loadProfile()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ProfileTopBar(currentScreen = "Profile") },
        bottomBar = { BottomNavigationProfile(navToSettingsScreen = onNavToSettingsPage, navToHomeScreen = onNavToHomePage, navToFriendsScreen = onNavToFriendsPage, navToSearchScreen = onNavToSearchPage) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 0.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.3f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (profileUiState.imageUrl != "") {
                        coilImage2(
                            url = profileUiState.imageUrl,
                            modifier = Modifier

                                .clickable(
                                    onClick = {
                                        singlePhotoLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ),
                            shape = RectangleShape
                        )
                    } else if (profileUiState.imageUri != null) {
                        coilImage2(
                            uri = profileUiState.imageUri,
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        singlePhotoLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ),
                            shape = RectangleShape
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        singlePhotoLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ),
                            shape = RectangleShape
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Image!",
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Column(
                    Modifier
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
                                    BasicTextField(
                                        value = profileUiState.userName,
                                        onValueChange = { profileViewmodel?.onUserNameChange(it) },
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .fillMaxHeight(0.5f),
                                        textStyle = TextStyle(
                                            color = MaterialTheme.colors.primary,
                                            fontSize = 16.sp
                                        ),
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
                                    BasicTextField(
                                        value = profileUiState.bio,
                                        onValueChange = { profileViewmodel?.onBioChange(it) },
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .fillMaxHeight(0.5f),
                                        textStyle = TextStyle(
                                            color = MaterialTheme.colors.primary,
                                            fontSize = 16.sp
                                        ),
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
                                        text = profileUiState.email,
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

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = {
                        if (pickedPhoto == null && (profileUiState.imageUrl != "") ) {
                            scope.apply {
                                launch {
                                    profileViewmodel?.onImageChangeUrl(profileUiState.imageUrl)
                                    profileViewmodel?.updateProfile()
                                    profileViewmodel?.resetProfileUpdatedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Profile Updated")
                                }
                            }
                        } else if (pickedPhoto != null && (profileUiState.imageUrl != "") ) {
                            scope.apply {
                                launch {
                                    profileViewmodel?.onImageChange(pickedPhoto)
                                    profileViewmodel?.updateProfile()
                                    profileViewmodel?.resetProfileUpdatedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Profile Updated")
                                }
                            }
                        } else {
                            scope.apply {
                                launch {
                                    profileViewmodel?.onImageChange(pickedPhoto)
                                    profileViewmodel?.updateProfile()
                                    profileViewmodel?.resetProfileUpdatedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Profile Updated")
                                }
                            }
                        }
                              },
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.onSurface,
                    )
                ) {
                    Text(
                        text = "Save Changes",
                        color = MaterialTheme.colors.primary,
                        fontSize = 16.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily
                    )
                }
            }
        }
    }
}
