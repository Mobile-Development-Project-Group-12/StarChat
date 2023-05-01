package com.group12.starchat

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group12.starchat.view.screens.login.SignUpScreen
import com.group12.starchat.view.screens.login.SigninScreen
import com.group12.starchat.view.screens.main.*
import com.group12.starchat.viewModel.*

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    signinViewModel: SigninViewModel,
    homeViewModel: HomeViewModel,
    chatViewModel: ChatViewModel,
    signupViewModel: SignupViewModel,
    roomViewModel: RoomViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    friendsViewModel: FriendsViewModel,
    searchViewModel: SearchViewModel,
    viewProfileViewModel: ViewProfileViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        authGraph(
            navController,
            signinViewModel,
            signupViewModel
        )
        homeGraph(
            navController = navController,
            homeViewModel,
            chatViewModel,
            roomViewModel,
            settingsViewModel,
            profileViewModel,
            friendsViewModel,
            searchViewModel,
            viewProfileViewModel
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    signinViewModel: SigninViewModel,
    signupViewModel: SignupViewModel
) {
    navigation(
        startDestination = "signin",
        route = "login"
    ) {
        composable(route = "signin") {
            SigninScreen(
                NavigateToHome = {
                    navController.navigate("main") {
                        launchSingleTop = true
                        popUpTo(route = "signin") {
                            inclusive = true
                        }
                    }
                },
                NavigateToSignup = {
                    navController.navigate("signup") {
                        launchSingleTop = true
                        popUpTo("signin") {
                            inclusive = true
                        }
                    }
                },
                signinViewMdoel = signinViewModel,
            )
        }

        composable(route = "signup") {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate("main") {
                        popUpTo("signup") {
                            inclusive = true
                        }
                    }
                },
                signupViewModel = signupViewModel,
            ) {
                navController.navigate("signin")
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    chatViewModel: ChatViewModel,
    roomViewmodel: RoomViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    friendsViewModel: FriendsViewModel,
    searchViewModel: SearchViewModel,
    viewProfileViewModel: ViewProfileViewModel
) {
    navigation(
        startDestination = "home",
        route = "main",
    ) {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onNavToLoginPage = {
                    navController.navigate("login") {
                        launchSingleTop = true
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onRoomClick = { chatNo ->
                    navController.navigate("chat?id=$chatNo")
                },
                navToRoom = {
                    navController.navigate("chat")
                },
                navToRoomEdit = { },
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onNavToProfilePage = {
                    navController.navigate("profile")
                },
                onNavToFriendsPage = {
                    navController.navigate("friends")
                },
                onNavToSearchPage = {
                    navController.navigate("search")
                }
            )
        }
        composable(
            route = "chat?id={roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            if (it.arguments?.getString("roomId")?.isNotEmpty() == true) {
                ChatScreen(
                    chatViewModel = chatViewModel,
                    roomId = it.arguments?.getString("roomId") as String,
                    navController = navController,
                    navToRoomEdit = { roomNo ->
                        navController.navigate("roomEdit?id=${roomNo}")
                    },
                )
            } else {
                RoomScreen(
                    navController = navController,
                    roomViewmodel = roomViewmodel,
                    roomId = it.arguments?.getString("roomId") as String,
                )
            }
        }
        composable(
            route = "settings",
        ) {
            SettingsScreen(
                settingsViewmodel = settingsViewModel,
                onNavToProfilePage = {
                    navController.navigate("profile")
                },
                onNavToFriendsPage = {
                    navController.navigate("friends")
                },
                onNavToHomePage = {
                    navController.navigate("home")
                },
                onNavToSearchPage = {
                    navController.navigate("search")
                }
            )
        }
        composable(
            route = "profile",
        ) {
            ProfileScreen(
                profileViewmodel = profileViewModel,
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onNavToSearchPage = {
                    navController.navigate("search")
                },
                onNavToHomePage = {
                    navController.navigate("home")
                },
                onNavToFriendsPage = {
                    navController.navigate("friends")
                }
            )
        }
        composable(
            route = "search",
        ) {
            SearchForFriendsScreen(
                searchViewModel = searchViewModel,
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onNavToProfilePage = {
                    navController.navigate("profile")
                },
                onNavToHomePage = {
                    navController.navigate("home")
                },
                onNavToFriendsPage = {
                    navController.navigate("friends")
                },
                onNavToUserProfile = {
                    navController.navigate("userProfile?id=$it")
                }
            )
        }

        composable(
            route = "friends",
        ) {
            FriendsScreen(
                friendsViewModel = friendsViewModel,
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onNavToProfilePage = {
                    navController.navigate("profile")
                },
                onNavToHomePage = {
                    navController.navigate("home")
                },
                onNavToSearchPage = {
                    navController.navigate("search")
                },
                onNavToUserProfile = {
                    navController.navigate("userProfile?id=$it")
                }
            )
        }

        composable(
            route = "userProfile?id={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            ViewProfileScreen(
                viewProfileViewModel = viewProfileViewModel,
                navController = navController,
                userId = it.arguments?.getString("userId") as String,
            )
        }

        composable(
            route = "roomEdit?id={roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            RoomScreen(
                roomViewmodel = roomViewmodel,
                roomId = it.arguments?.getString("roomId") as String,
                navController = navController
            )
        }
    }
}