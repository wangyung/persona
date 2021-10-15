package com.github.wangyung.app.model

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.RandomizeParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import com.github.wangyung.app.transformation.BlinkParticleTransformation
import com.github.wangyung.app.transformation.HorizontalSpeedWithSinParticleTransformation
import com.github.wangyung.app.transformation.LinearScaleParticleTransformation
import com.github.wangyung.app.transformation.ScaleAndDimParticleTransformation
import com.github.wangyung.persona.app.R
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

internal const val DEFAULT_FLYGINBIRD_ANGLE_FROM = 175
internal const val DEFAULT_FLYGINBIRD_ANGLE_TO = 185

sealed class AnimationType(val value: String) {
    object Rain : AnimationType(RAIN)
    object Snow : AnimationType(SNOW)
    object Sakura : AnimationType(SAKURA)
    object FlyingPoo : AnimationType(FLYING_POO)
    object FlyingMoney : AnimationType(FLYING_MONEY)
    object FlyingBird : AnimationType(FLYING_BIRD)
    object TwinkleStar : AnimationType(TWINKLE_STAR)
    object Emotion : AnimationType(EMOTION)
    object Confetti : AnimationType(CONFETTI)

    override fun toString(): String = this.value

    @Composable
    fun toGeneratorParameters(
        resources: Resources = LocalContext.current.resources
    ): RandomizeParticleGeneratorParameters =
        when (this) {
        is Snow -> snowParameters
        is Sakura -> sakuraParameters
        is Rain -> rainParameters
        is FlyingPoo -> pooParameters
        is FlyingMoney -> moneyParameters
        is FlyingBird -> createFlyingBirdParameters(resources = resources)
        is TwinkleStar -> twinkleStarParameters
        is Emotion -> emotionParameters
        is Confetti -> confettiParameters
    }

    fun toParticleSystemParameters(): ParticleSystemParameters = when (this) {
        is Sakura -> sakuraSystemParameters
        is Emotion -> emotionSystemParameters
        else -> defaultSystemParameters
    }

    fun toParticleTransformation(): ParticleTransformation = when (this) {
        Rain -> LinearTranslateTransformation()
        FlyingMoney -> CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation()
            )
        )
        Snow -> CompositeTransformation(
            listOf(
                HorizontalSpeedWithSinParticleTransformation(),
                LinearScaleParticleTransformation(),
            )
        )
        Sakura -> CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation(),
            )
        )
        FlyingPoo -> CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation(),
            )
        )
        TwinkleStar -> BlinkParticleTransformation(frequencyFactorRange = 0.5f..2f)
        Emotion -> SequenceTransformation().apply {
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
        Confetti -> CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation(),
            )
        )
        else -> defaultTransformation
    }

    fun toTitle(): String =
        when (this) {
            is Snow -> "Snow"
            is Sakura -> "Sakura (桜吹雪)"
            is Rain -> "Rain"
            is FlyingPoo -> "Flying Foo"
            is FlyingMoney -> "Flying Money"
            is FlyingBird -> "Flying Bird"
            is TwinkleStar -> "Twinkle Star"
            is Emotion -> "Instagram-like emotion"
            is Confetti -> "Confetti"
        }
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
    angleRange = 60..120,
    xRotationalSpeedRange = 0.1f..0.5f,
    zRotationalSpeedRange = DEFAULT_POO_MIN_ROTATIONAL_SPEED..DEFAULT_POO_MAX_ROTATIONAL_SPEED,
    sourceEdges = setOf(SourceEdge.TOP, SourceEdge.LEFT, SourceEdge.RIGHT),
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
    angleRange = -10..10,
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
