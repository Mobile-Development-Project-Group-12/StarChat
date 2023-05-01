package com.group12.starchat.view.screens.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.viewModel.SignupViewModel

@Composable
fun SignUpScreen(
    signupViewModel: SignupViewModel,
    onNavToHomePage:() -> Unit,
    onNavToLoginPage:() -> Unit,
) {
    val signupUiState = signupViewModel?.signupUiState
    val isError = signupUiState?.signUpError != null
    val context = LocalContext.current

    val signupTextfieldStyle: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
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

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    if (pickedPhoto != null) {
        // Image selected
        signupViewModel.onImageChange(pickedPhoto)
    } else {
        // No image selected
        signupViewModel.onImageChange(null)
    }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.primary,
            title = {
                Text(
                    text = "SignUp",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
        )

        if (isError){
            Text(
                text = signupUiState?.signUpError ?: "unknown error",
                color = Color.Red,
            )
        }

        Text(
            text = "Choose your profile picture!",
            modifier = Modifier.padding(16.dp),
        )

        if (pickedPhoto != null) {
            coilImage(uri = pickedPhoto, modifier = Modifier
                .padding(1.dp)
                .size(100.dp)
                .clickable(
                    onClick = {
                        singlePhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                ),
                shape = RoundedCornerShape(100),
            )
        } else {
            Card(
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .padding(1.dp)
                    .size(100.dp)
                    .clickable(
                        onClick = {
                            singlePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    ),
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

        /**
         * Sign Up Form
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signupUiState?.userName ?: "",
                onValueChange = {signupViewModel?.onUserNameChange(it)},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = "Username")
                },
                isError = isError,
                colors = signupTextfieldStyle
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signupUiState?.email ?: "",
                onValueChange = {signupViewModel?.onEmailChange(it)},
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
                colors = signupTextfieldStyle
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signupUiState?.password ?: "",
                onValueChange = { signupViewModel?.onPasswordChangeSignup(it) },
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
                colors = signupTextfieldStyle
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                value = signupUiState?.confirmPassword ?: "",
                onValueChange = { signupViewModel?.onConfirmPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = "Confirm Password")
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError,
                colors = signupTextfieldStyle
            )

            Button(
                onClick = { signupViewModel?.createUser(context) },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            ) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colors.secondary)
            }

            //Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an Account?",
                    color = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier)
                TextButton(onClick = { onNavToLoginPage.invoke() }) {
                    Text(text = "Sign In", fontSize = 12.sp, color = MaterialTheme.colors.primary)
                }

            }

            if (signupUiState?.isLoading == true){
                CircularProgressIndicator()
            }

            LaunchedEffect(key1 = signupViewModel?.hasUser){
                if (signupViewModel?.hasUser == true){
                    onNavToHomePage.invoke()
                }
            }
        }
    }
}



@Preview(widthDp = 360, heightDp = 640, showBackground = true, name = "Sign Up Screen")
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(onNavToHomePage = {}, onNavToLoginPage = {}, signupViewModel = SignupViewModel())
}