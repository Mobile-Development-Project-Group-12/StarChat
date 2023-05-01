package com.group12.starchat.view.components.topBars

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.R
import com.group12.starchat.view.theme.fontfamily

@Composable
fun HomeTopBar(
    appTitle: String,
    onSignOutClick: () -> Unit,
) {

    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp,
        modifier = Modifier,
            //.shadow(10.dp),
        actions = {
            Box(
                Modifier
                    .width(64.dp)
            ) {
                IconButton(
                    onClick = {
                        onSignOutClick()
                    }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "Sign Out",
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        navigationIcon = {
            Box(
                Modifier
                    .width(64.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "StarChat Logo",
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        },
        title = {
            Text(
                text = appTitle,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.primary,
                fontFamily = fontfamily
            )
        }
    )
}
