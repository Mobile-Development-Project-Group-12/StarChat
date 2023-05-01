package com.group12.starchat.view.components.topBars

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.view.theme.fontfamily
import com.group12.starchat.viewModel.FriendsViewModel
import com.group12.starchat.viewModel.listState

@Composable
fun FriendsTopBar(
    currentScreen: String,
    friendsViewModel: FriendsViewModel?,
) {

    val dropDownState = remember { mutableStateOf(false) }

    val iconDropDown = if (dropDownState.value) {
        Icons.Filled.ArrowDropUp
    } else {
        Icons.Filled.ArrowDropDown
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp,
        modifier = Modifier
            .shadow(10.dp),
        title = {
            Text(
                text = currentScreen,
                style = TextStyle(
                    color = MaterialTheme.colors.primary,
                    fontSize = 20.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            IconButton(onClick = {
                dropDownState.value = !dropDownState.value
            }) {
                Icon(
                    imageVector = iconDropDown,
                    contentDescription = "Choose List",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(expanded = dropDownState.value, onDismissRequest = { dropDownState.value = false }) {

                DropdownMenuItem(
                    onClick = { friendsViewModel?.onListChange(listState.FRIENDS) },
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Icon(imageVector = Icons.Filled.People, contentDescription = "Friends Icon", tint = MaterialTheme.colors.primary)

                        Text(
                            text = "Friends",
                            style = TextStyle(
                                color = MaterialTheme.colors.primary,
                                fontSize = 12.sp,
                                fontFamily = fontfamily
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                DropdownMenuItem(onClick = { friendsViewModel?.onListChange(listState.BLOCKED) }) {
                    Row {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Blocked Icon", tint = MaterialTheme.colors.primary)

                        Text(
                            text = "Blocked",
                            style = TextStyle(
                                color = MaterialTheme.colors.primary,
                                fontSize = 12.sp,
                                fontFamily = fontfamily
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

        }

    )
}