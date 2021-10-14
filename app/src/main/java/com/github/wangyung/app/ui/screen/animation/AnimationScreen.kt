package com.github.wangyung.app.ui.screen.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.ui.theme.PersonaDemoAppTheme

@Composable
fun AnimationScreen(animationType: AnimationType?) {
    PersonaDemoAppTheme {
        // Use Snow for default
        when (animationType) {
            is AnimationType.Sakura -> SakuraDemo()
            is AnimationType.Snow -> SnowDemo()
            is AnimationType.Rain -> RainDemo()
            is AnimationType.Emotion -> EmotionDemo()
            is AnimationType.TwinkleStar -> TwinkleStarDemo()
            is AnimationType.FlyingPoo -> FlyingPooDemo()
            is AnimationType.FlyingBird -> FlyingBirdDemo()
            is AnimationType.Confetti -> ConfettiDemo()
            else -> SnowDemo()
        }
    }
}

@Composable
@Preview
fun AnimationScreen() {
    AnimationScreen(AnimationType.Snow)
}
