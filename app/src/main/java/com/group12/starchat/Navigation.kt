package com.group12.starchat

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group12.starchat.view.screens.login.SignUpScreen
import com.group12.starchat.view.screens.login.SigninScreen
import com.group12.starchat.view.screens.main.ChatScreen
import com.group12.starchat.view.screens.main.HomeScreen
import com.group12.starchat.viewModel.HomeViewModel
import com.group12.starchat.viewModel.SigninViewModel
import com.group12.starchat.viewModel.SignupViewModel
import io.getstream.chat.android.client.ChatClient

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    signinViewModel: SigninViewModel,
    homeViewModel: HomeViewModel,
    signupViewModel: SignupViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        authGraph(
            navController,
            signinViewModel,
            signupViewModel
        )
        homeGraph(
            navController = navController,
            homeViewModel,
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
) {
    navigation(
        startDestination = "home",
        route = "main",
    ) {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onSignOutClick = {
                    navController.navigate("signin") {
                        launchSingleTop = true
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onChannelClick = { channel ->
                    navController.navigate("channel?id=$channel") {
                        launchSingleTop = true
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = "channel?id={channel}",
            arguments = listOf(navArgument("channel") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { channel ->
            //channel.arguments?.getString("channel")?.let { ChatScreen(it) }
        }
    }
}
