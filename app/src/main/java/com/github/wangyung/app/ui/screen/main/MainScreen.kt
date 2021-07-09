package com.github.wangyung.app.ui.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationInfo
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.model.animationDemos
import com.github.wangyung.app.ui.theme.PersonaDemoAppTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun MainScreen(navigateToAnimationDemo: (AnimationType) -> Unit) {
    PersonaDemoAppTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally),
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false
            )
        ) {
            item {
                AnimationCardSection(
                    animationInfos = animationDemos,
                    navigateToAnimationDemo = navigateToAnimationDemo
                )
            }
        }
    }
}

@Composable
fun AnimationCardSection(
    animationInfos: List<AnimationInfo>,
    navigateToAnimationDemo: (AnimationType) -> Unit
) {
    animationInfos.forEach {
        AnimationDescriptionCard(animationInfo = it, navigateToAnimationDemo = navigateToAnimationDemo)
        Divider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
        )
    }
}

@Preview("Default")
@Preview("Font scaling 1.5", fontScale = 1.5f)
@Preview("Dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreview() {
    PersonaDemoAppTheme {
        MainScreen({})
    }
}
