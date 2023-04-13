package com.group12.starchat.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.ServerCode

@Preview
@Composable
fun TestScreen() {
    Scaffold() {

        var instanceTypeBeat = ServerCode()
        var repository = DatabaseRepo()

        Column(
            modifier = Modifier.padding(it)
        ) {
            repository.MyFunction()
            Text(text = "Function called", textAlign = TextAlign.Center)
        }
    }
}
