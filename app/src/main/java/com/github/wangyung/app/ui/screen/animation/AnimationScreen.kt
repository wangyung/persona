package com.github.wangyung.app.ui.screen.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.ui.theme.PersonaDemoAppTheme

@Composable
fun AnimationScreen(animationType: AnimationType?) {
    PersonaDemoAppTheme {
        animationType?.DemoScreen()
    }
}

@Composable
@Preview
fun AnimationScreen() {
    AnimationScreen(AnimationType.Snow)
}
