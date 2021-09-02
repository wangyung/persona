package com.github.wangyung.app.ui

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.ui.theme.PersonaDemoAppTheme
import com.github.wangyung.persona.app.R
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PersonaDemoApp() {
    PersonaDemoAppTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
            }
            Scaffold(topBar = {
                val title = stringResource(id = R.string.app_name)
                TopBar(title = title)
            }) {
                PersonaDemoNavGraph()
            }
        }
    }
}

@Composable
fun TopBar(title: String) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
    ) {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null
                )
            },
            backgroundColor = MaterialTheme.colors.primarySurface,
            contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.primarySurface),
            elevation = 0.dp,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(title = "Test")
}
