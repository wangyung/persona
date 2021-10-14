package com.github.wangyung.app.model

import androidx.annotation.DrawableRes
import com.github.wangyung.persona.app.R

data class AnimationInfo(
    @DrawableRes val thumbnailId: Int,
    val title: String,
    val description: String,
    val animationType: AnimationType
)

@Suppress("MaxLineLength")
val animationDemos: List<AnimationInfo> = listOf(
    AnimationInfo(
        thumbnailId = R.drawable.sakura_animation,
        title = AnimationType.Sakura.toTitle(),
        description = "The shower of sakura blossoms. It uses Path for the particle.",
        animationType = AnimationType.Sakura,
    ),
    AnimationInfo(
        thumbnailId = R.drawable.snow_animation,
        title = AnimationType.Snow.toTitle(),
        description = "A snowing animation. It uses the Circle for the particle.",
        animationType = AnimationType.Snow,
    ),
    AnimationInfo(
        thumbnailId = R.drawable.rain_animation,
        title = AnimationType.Rain.toTitle(),
        description = "A raining animation. It uses the Line for the particle.",
        animationType = AnimationType.Rain,
    ),
    AnimationInfo(
        thumbnailId = R.drawable.poo_animation,
        title = AnimationType.FlyingPoo.toTitle(),
        description = """
            A flying poo animation. 
            It uses the Text(emoji) for the particle and demonstrates how to use CompositeTransformation to apply transformations together. 
        """.trimIndent(),
        animationType = AnimationType.FlyingPoo,
    ),
    AnimationInfo(
        thumbnailId = R.drawable.bird_animation,
        title = AnimationType.FlyingBird.toTitle(),
        description = """
            A flying bird animation. 
            It uses the Image for the particle and demonstrates how to use SourceEdge for the particle.
        """.trimIndent(),
        animationType = AnimationType.FlyingBird,
    ),
    AnimationInfo(
        thumbnailId = R.drawable.star_animation,
        title = AnimationType.TwinkleStar.toTitle(),
        description = """
            The animation of the twinkle star. 
            It uses Circle for the particle and demonstrates how to use StartOffset for the particle.
        """.trimIndent(),
        animationType = AnimationType.TwinkleStar,
    ),

    AnimationInfo(
        thumbnailId = R.drawable.emotion_animation,
        title = AnimationType.Emotion.toTitle(),
        description = """
            An Instagram-like animation. 
            It demonstrates how to use SequenceTransformation to apply the different transformations sequentially. 
        """.trimIndent(),
        animationType = AnimationType.Emotion,
    ),

    AnimationInfo(
        thumbnailId = R.drawable.confetti_animation,
        title = AnimationType.Confetti.toTitle(),
        description = """
            The confetti effects. 
        """.trimIndent(),
        animationType = AnimationType.Confetti,
    ),
)
