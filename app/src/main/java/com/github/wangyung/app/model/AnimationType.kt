package com.github.wangyung.app.model

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wangyung.app.transformation.BlinkParticleTransformation
import com.github.wangyung.app.transformation.HorizontalSpeedWithSinParticleTransformation
import com.github.wangyung.app.transformation.LinearScaleParticleTransformation
import com.github.wangyung.app.transformation.ScaleAndDimParticleTransformation
import com.github.wangyung.app.ui.screen.animation.ConfettiDemo
import com.github.wangyung.app.ui.screen.animation.EmotionDemo
import com.github.wangyung.app.ui.screen.animation.FlyingBirdDemo
import com.github.wangyung.app.ui.screen.animation.FlyingPooDemo
import com.github.wangyung.app.ui.screen.animation.RainDemo
import com.github.wangyung.app.ui.screen.animation.SakuraDemo
import com.github.wangyung.app.ui.screen.animation.SnowDemo
import com.github.wangyung.app.ui.screen.animation.TwinkleStarDemo
import com.github.wangyung.persona.app.R
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.RandomizeParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import com.github.wangyung.persona.particle.transformation.CompositeTransformation
import com.github.wangyung.persona.particle.transformation.LinearRotationTransformation
import com.github.wangyung.persona.particle.transformation.LinearTranslateTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import com.github.wangyung.persona.particle.transformation.SequenceTransformation

private const val RAIN = "Rain"
private const val SNOW = "Snow"
private const val SAKURA = "Sakura"
private const val FLYING_POO = "FlyingPoo"
private const val FLYING_MONEY = "FlyingMoney"
private const val FLYING_BIRD = "FlyingBird"
private const val TWINKLE_STAR = "TwinkleStart"
private const val EMOTION = "Emotion"
private const val CONFETTI = "Confetti"

internal const val DEFAULT_SNOW_MIN_RADIUS = 5
internal const val DEFAULT_SNOW_MAX_RADIUS = 10

internal const val DEFAULT_RAIN_MIN_SPEED = 10f
internal const val DEFAULT_RAIN_MAX_SPEED = 30f
internal const val DEFAULT_RAIN_ANGLE_FROM = 85
internal const val DEFAULT_RAIN_ANGLE_TO = 95

internal const val DEFAULT_POO_MIN_ROTATIONAL_SPEED = 0.5f
internal const val DEFAULT_POO_MAX_ROTATIONAL_SPEED = 5f

internal const val DEFAULT_FLYGING_BIRD_ANGLE_FROM = 175
internal const val DEFAULT_FLYGING_BIRD_ANGLE_TO = 185

sealed class AnimationType(val value: String) {
    abstract fun toGeneratorParameters(resources: Resources): RandomizeParticleGeneratorParameters
    abstract fun toTitle(): String
    open fun toParticleSystemParameters(): ParticleSystemParameters = defaultSystemParameters
    abstract fun toParticleTransformation(): ParticleTransformation
    @Composable
    abstract fun DemoScreen()

    object Rain : AnimationType(RAIN) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = rainParameters

        override fun toTitle(): String = "Rain"

        override fun toParticleTransformation(): ParticleTransformation =
            LinearTranslateTransformation()

        @Composable
        override fun DemoScreen() = RainDemo()
    }

    object Snow : AnimationType(SNOW) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = snowParameters

        override fun toTitle(): String = "Snow"

        override fun toParticleTransformation(): ParticleTransformation = CompositeTransformation(
            listOf(
                HorizontalSpeedWithSinParticleTransformation(),
                LinearScaleParticleTransformation(),
            )
        )

        @Composable
        override fun DemoScreen() = SnowDemo()
    }

    object Sakura : AnimationType(SAKURA) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = sakuraParameters

        override fun toParticleSystemParameters(): ParticleSystemParameters =
            sakuraSystemParameters

        override fun toTitle(): String = "Sakura (桜吹雪)"

        override fun toParticleTransformation(): ParticleTransformation = CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation(),
            )
        )

        @Composable
        override fun DemoScreen() = SakuraDemo()
    }

    object FlyingPoo : AnimationType(FLYING_POO) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = pooParameters

        override fun toTitle(): String = "Flying Foo"

        override fun toParticleTransformation(): ParticleTransformation = CompositeTransformation(
            listOf(
                LinearTranslateTransformation(gravity = 0.1f),
                LinearRotationTransformation(),
            )
        )

        @Composable
        override fun DemoScreen() = FlyingPooDemo()
    }

    object FlyingMoney : AnimationType(FLYING_MONEY) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = moneyParameters

        override fun toTitle(): String = "Flying Money"

        override fun toParticleTransformation(): ParticleTransformation = CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation()
            )
        )

        @Composable
        override fun DemoScreen() = TODO("Not yet implemented")
    }

    object FlyingBird : AnimationType(FLYING_BIRD) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = createFlyingBirdParameters(resources)

        override fun toTitle(): String = "Flying Bird"

        override fun toParticleTransformation(): ParticleTransformation = defaultTransformation

        @Composable
        override fun DemoScreen() = FlyingBirdDemo()
    }

    object TwinkleStar : AnimationType(TWINKLE_STAR) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = twinkleStarParameters

        override fun toTitle(): String = "Twinkle Star"

        override fun toParticleTransformation(): ParticleTransformation =
            BlinkParticleTransformation(frequencyFactorRange = 0.5f..2f)

        @Composable
        override fun DemoScreen() = TwinkleStarDemo()
    }

    object Emotion : AnimationType(EMOTION) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = emotionParameters

        override fun toParticleSystemParameters(): ParticleSystemParameters =
            emotionSystemParameters

        override fun toTitle(): String = "Instagram-like emotion"

        override fun toParticleTransformation(): ParticleTransformation =
            SequenceTransformation().apply {
                val scaleDuration = 30L
                add(LinearTranslateTransformation(), 40L)
                add(
                    ScaleAndDimParticleTransformation(
                        1f / scaleDuration,
                        1f / scaleDuration,
                        alphaDelta = 1f / scaleDuration
                    ),
                    scaleDuration
                )
            }

        @Composable
        override fun DemoScreen() = EmotionDemo()
    }

    object Confetti : AnimationType(CONFETTI) {
        override fun toGeneratorParameters(
            resources: Resources
        ): RandomizeParticleGeneratorParameters = confettiParameters

        override fun toTitle(): String = "Confetti"

        override fun toParticleTransformation(): ParticleTransformation = CompositeTransformation(
            listOf(
                LinearTranslateTransformation(gravity = 0.2f),
                LinearRotationTransformation(),
            )
        )

        @Composable
        override fun DemoScreen() = ConfettiDemo()
    }

    override fun toString(): String = this.value
}

fun String.toAnimationType(): AnimationType? =
    when (this) {
        SNOW -> AnimationType.Snow
        SAKURA -> AnimationType.Sakura
        RAIN -> AnimationType.Rain
        FLYING_POO -> AnimationType.FlyingPoo
        FLYING_MONEY -> AnimationType.FlyingMoney
        FLYING_BIRD -> AnimationType.FlyingBird
        TWINKLE_STAR -> AnimationType.TwinkleStar
        EMOTION -> AnimationType.Emotion
        CONFETTI -> AnimationType.Confetti
        else -> null
    }

private val defaultTransformation = LinearTranslateTransformation()

internal val sakuraParameters = RandomizeParticleGeneratorParameters(
    count = 40,
    particleWidthRange = 10..20,
    particleHeightRange = 10..20,
    speedRange = 2f..8f,
    scaleRange = 1.0f..1.0f,
    angleRange = 95..140,
    xRotationalSpeedRange = 0.1f..0.5f,
    zRotationalSpeedRange = -1f..-0.1f,
    sourceEdges = setOf(SourceEdge.TOP, SourceEdge.RIGHT),
    shapeProvider = { createSakuraParticle(strokeRange = 1..3) },
)

internal val snowParameters = RandomizeParticleGeneratorParameters(
    randomizeInitialXY = true,
    count = 125,
    speedRange = 1f..2f,
    angleRange = 80..100,
    zRotationalSpeedRange = 0f..0f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = { createShowParticle(DEFAULT_SNOW_MIN_RADIUS..DEFAULT_SNOW_MAX_RADIUS) },
)

internal val rainParameters = RandomizeParticleGeneratorParameters(
    count = 400,
    particleWidthRange = 1..2,
    particleHeightRange = 10..20,
    speedRange = DEFAULT_RAIN_MIN_SPEED..DEFAULT_RAIN_MAX_SPEED,
    angleRange = DEFAULT_RAIN_ANGLE_FROM..DEFAULT_RAIN_ANGLE_TO,
    zRotationalSpeedRange = 0f..0f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = { createRainParticle(2..6) },
)
internal val pooParameters = RandomizeParticleGeneratorParameters(
    count = 30,
    particleWidthRange = 1..10,
    particleHeightRange = 1..10,
    speedRange = 3f..10f,
    angleRange = 315..330,
    randomizeInitialXY = false,
    xRotationalSpeedRange = 0.1f..0.5f,
    zRotationalSpeedRange = DEFAULT_POO_MIN_ROTATIONAL_SPEED..DEFAULT_POO_MAX_ROTATIONAL_SPEED,
    sourceEdges = setOf(SourceEdge.LEFT),
    shapeProvider = {
        ParticleShape.Text(
            text = "\uD83D\uDCA9", // poopoo
            fontSize = 14.sp,
            borderWidth = 1.dp,
            color = Color.Black,
        )
    },
)

internal val moneyParameters = RandomizeParticleGeneratorParameters(
    count = 30,
    particleWidthRange = 1..10,
    particleHeightRange = 1..10,
    speedRange = 3f..10f,
    angleRange = 45..135,
    zRotationalSpeedRange = 1f..3f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = {
        createMoneyParticle(fontSizeRange = 9..24)
    },
)

internal fun createFlyingBirdParameters(resources: Resources) =
    RandomizeParticleGeneratorParameters(
        count = 5,
        randomizeInitialXY = false,
        particleWidthRange = 100..200,
        particleHeightRange = 80..100,
        speedRange = 5f..30f,
        angleRange = 175..185,
        zRotationalSpeedRange = 0f..0f,
        sourceEdges = setOf(SourceEdge.RIGHT),
        shapeProvider = {
            createBirdParticle(resources, R.drawable.flying_bird)
        },
    )

internal val twinkleStarParameters = RandomizeParticleGeneratorParameters(
    count = 250,
    particleWidthRange = 1..4,
    particleHeightRange = 1..4,
    sourceEdges = setOf(SourceEdge.TOP),
    startOffsetRange = 0..60,
    shapeProvider = { createStarParticle(Color(0xFFFAFFFF), 1..4) },
)

internal val emotionParameters = RandomizeParticleGeneratorParameters(
    count = 16,
    randomizeInitialXY = false,
    particleWidthRange = 1..10,
    particleHeightRange = 1..10,
    speedRange = 10f..15f,
    angleRange = 265..275,
    sourceEdges = setOf(SourceEdge.BOTTOM),
    startOffsetRange = 0..30,
    shapeProvider = {
        ParticleShape.Text(
            text = "\uD83D\uDE0D", // heart-eyes emoji
            fontSize = 20.sp,
            borderWidth = 1.dp,
            color = Color.White.copy(alpha = 1f),
        )
    },
)

internal val confettiParameters = RandomizeParticleGeneratorParameters(
    count = 200,
    particleWidthRange = 10..20,
    particleHeightRange = 10..20,
    speedRange = 2f..8f,
    angleRange = 315..330,
    xRotationalSpeedRange = 0.1f..0.5f,
    zRotationalSpeedRange = 0.1f..1f,
    sourceEdges = setOf(SourceEdge.LEFT),
    shapeProvider = {
        createConfettiParticle(
            colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
        )
    },
)

internal val defaultSystemParameters = ParticleSystemParameters()
internal val sakuraSystemParameters = ParticleSystemParameters(fps = 60)
internal val emotionSystemParameters = ParticleSystemParameters(
    autoResetParticles = false,
    restartWhenAllDead = false,
)
