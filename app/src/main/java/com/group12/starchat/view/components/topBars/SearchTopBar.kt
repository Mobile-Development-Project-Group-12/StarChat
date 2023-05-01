package com.group12.starchat.view.components.topBars

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.view.theme.fontfamily
import com.group12.starchat.viewModel.SearchBarState
import com.group12.starchat.viewModel.SearchViewModel
import com.group12.starchat.viewModel.SearchingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreenTopBar(
    currentScreen: String,
    searchViewModel: SearchViewModel?,
) {

    TopAppBar(
        actions = {
            IconButton(onClick = {
                searchViewModel?.onSearchBarChange(SearchBarState.Open)
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "SearchButton",
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        title = {
            Text(
                text = currentScreen,
                style = TextStyle(
                    fontFamily = fontfamily,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.primary
                )
            )
        },
        backgroundColor = MaterialTheme.colors.onSurface,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchQueryTopBar(
    searchViewModel: SearchViewModel,
) {

    var searchIcon = if (searchViewModel.searchQuery.isNotEmpty()) {
        Icons.Filled.Clear
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    // MutableTransitionState<Boolean> variable
    val visibleState = remember { MutableTransitionState(false) }

    var requestClose by remember { mutableStateOf(false) }

    // focus requester
    val focusRequester = FocusRequester()

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(stiffness = 1000f)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(stiffness = 1000f)
            )
        ) {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        if (searchViewModel.searchQuery.isNotEmpty()) {
                            searchViewModel.onSearchQueryChange("")
                        } else {
                            requestClose = true
                        }
                    }) {
                        Icon(
                            imageVector = searchIcon,
                            contentDescription = "Clear Query or Close Bar",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                title = {
                    TextField(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "SearchButton",
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier
                                    .alpha(0.5f)
                            )
                        },
                        value = searchViewModel.searchQuery,
                        onValueChange = {
                            scope.apply {
                                launch {
                                    searchViewModel.onSearchQueryChange(it)
                                    searchViewModel.onSearchingStateChange(SearchingState.Searching)
                                    delay(300)
                                    searchViewModel.searchUsersByName()
                                }
                            }
                                        },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                            //.focusRequester(focusRequester),
                        shape = RoundedCornerShape(100),
                        placeholder = {
                            Text(
                                text = "Search",
                                style = TextStyle(
                                    fontFamily = fontfamily,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colors.onSurface
                                )
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = fontfamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search,
                            keyboardType = KeyboardType.Text
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            focusedIndicatorColor = MaterialTheme.colors.background,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colors.onSurface,
                            textColor = MaterialTheme.colors.onSurface,
                            placeholderColor = MaterialTheme.colors.onSurface
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.onSurface,
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        visibleState.targetState = true
        //delay(300)
        //focusRequester.requestFocus()
    }

    // launched effect for exit search query bar
    LaunchedEffect(key1 = requestClose) {
        if (requestClose) {
            visibleState.targetState = false
            delay(300)
            searchViewModel.onSearchBarChange(SearchBarState.Closed)
        }
    }
}