package com.group12.starchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group12.starchat.view.theme.StarChatTheme
import com.group12.starchat.viewModel.HomeViewModel
import com.group12.starchat.viewModel.SigninViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val signinViewModel = viewModel(modelClass = SigninViewModel::class.java)
            val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
            StarChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(
                        signinViewModel = signinViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
