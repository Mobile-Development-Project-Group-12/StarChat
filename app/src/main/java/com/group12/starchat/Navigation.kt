package com.group12.starchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.group12.starchat.view.screens.login.SigninScreen
import com.group12.starchat.view.screens.main.HomeScreen
import com.group12.starchat.viewModel.HomeViewModel
import com.group12.starchat.viewModel.SigninViewModel

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    signinViewModel: SigninViewModel,
    homeViewModel: HomeViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        authGraph(navController, signinViewModel)
        homeGraph(
            navController = navController,
            homeViewModel,
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    signinViewModel: SigninViewModel,
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
                }
            )
        }
    }
}