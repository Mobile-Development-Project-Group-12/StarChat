package com.group12.starchat.view.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.R
import com.group12.starchat.viewModel.SigninViewModel

@Composable
fun SigninScreen(
    signinViewMdoel: SigninViewModel? = null,
    NavigateToHome:() -> Unit,
    NavigateToSignup: () -> Unit,
) {
    
    val signinUiState = signinViewMdoel?.loginUiState
    val isError = signinUiState?.loginError != null
    val context = LocalContext.current

    val loginTextfieldStyle: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colors.primary,
        unfocusedBorderColor = MaterialTheme.colors.primary,
        focusedLabelColor = MaterialTheme.colors.primary,
        unfocusedLabelColor = MaterialTheme.colors.primary,
        cursorColor = MaterialTheme.colors.primary,
        errorCursorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorLeadingIconColor = Color.Red,
        trailingIconColor = MaterialTheme.colors.primary,
        leadingIconColor = MaterialTheme.colors.primary,
        textColor = MaterialTheme.colors.primary,
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 139.dp)
        ) {

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Logo Background",
                    modifier = Modifier.fillMaxSize()
                )

                Image(
                    modifier = Modifier.fillMaxSize(0.75F),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo"
                )
            }

            Text(
                text = "StarChat",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary,
                fontSize = 30.sp
            )
        }

        if (isError){
            Text(
                text = signinUiState?.loginError ?: "unknown error",
                color = Color.Red,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signinUiState?.userName ?: "",
                onValueChange = {signinViewMdoel?.onUserNameChange(it)},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = "Email")
                },
                isError = isError,
                colors = loginTextfieldStyle
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signinUiState?.password ?: "",
                onValueChange = { signinViewMdoel?.onPasswordNameChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = "Password")
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError,
                colors = loginTextfieldStyle
            )

            Button(
                onClick = { signinViewMdoel?.loginUser(context) },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, top = 8.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    color = MaterialTheme.colors.secondary,

                    )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an Account?",
                    fontStyle = MaterialTheme.typography.body1.fontStyle,
                    color = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.size(8.dp))
                TextButton(onClick = { NavigateToSignup.invoke() }) {
                    Text(text = "SignUp", fontSize = 12.sp, color = MaterialTheme.colors.primary)
                }
            }
        }

        if (signinUiState?.isLoading == true){
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = signinViewMdoel?.hasUser){
            if (signinViewMdoel?.hasUser == true){
                NavigateToHome.invoke()
            }
        }

    }
}