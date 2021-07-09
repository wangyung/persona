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
import com.github.wangyung.persona.particle.transformation.*
import com.github.wangyung.app.transformation.BlinkParticleTransformation
import com.github.wangyung.app.transformation.HorizontalSpeedWithSinParticleTransformation
import com.github.wangyung.app.transformation.LinearScaleParticleTransformation
import com.github.wangyung.app.transformation.ScaleAndDimParticleTransformation
import com.github.wangyung.persona.app.R

private const val RAIN = "Rain"
private const val SNOW = "Snow"
private const val SAKURA = "Sakura"
private const val FLYING_POO = "FlyingPoo"
private const val FLYING_MONEY = "FlyingMoney"
private const val FLYING_BIRD = "FlyingBird"
private const val TWINKLE_STAR = "TwinkleStart"
private const val EMOTION = "Emotion"

internal const val DEFAULT_SNOW_MIN_RADIUS = 5
internal const val DEFAULT_SNOW_MAX_RADIUS = 10

internal const val DEFAULT_RAIN_MIN_SPEED = 10f
internal const val DEFAULT_RAIN_MAX_SPEED = 30f
internal const val DEFAULT_RAIN_ANGLE_FROM = 85
internal const val DEFAULT_RAIN_ANGLE_TO = 95

internal const val DEFAULT_POO_MIN_ROTATIONAL_SPEED = 1f
internal const val DEFAULT_POO_MAX_ROTATIONAL_SPEED = 15f

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
                LinearScaleParticleTransformation()
            )
        )
        FlyingPoo -> CompositeTransformation(
            listOf(
                LinearTranslateTransformation(),
                LinearRotationTransformation(),
                LinearScaleParticleTransformation()
            )
        )
        TwinkleStar -> BlinkParticleTransformation(
            minFrequencyFactor = 0.5f,
            maxFrequencyFactor = 2f
        )
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
        else -> null
    }

private val defaultTransformation = LinearTranslateTransformation()

internal val sakuraParameters = RandomizeParticleGeneratorParameters(
    count = 40,
    particleWidthRange = IntRange(10, 20),
    particleHeightRange = IntRange(10, 15),
    minSpeed = 2f,
    maxSpeed = 8f,
    minScale = 0.7f,
    maxScale = 1.5f,
    angleRange = IntRange(95, 140),
    minRotationalSpeed = 0.5f,
    maxRotationalSpeed = 3.5f,
    sourceEdges = setOf(SourceEdge.TOP, SourceEdge.RIGHT),
    shapeProvider = { createSakuraParticle(strokeRange = IntRange(1, 3)) },
)

internal val snowParameters = RandomizeParticleGeneratorParameters(
    randomizeInitialXY = true,
    count = 125,
    minSpeed = 1.0f,
    maxSpeed = 2.0f,
    angleRange = IntRange(80, 100),
    minRotationalSpeed = 0f,
    maxRotationalSpeed = 0f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = { createShowParticle(IntRange(DEFAULT_SNOW_MIN_RADIUS, DEFAULT_SNOW_MAX_RADIUS)) },
)

internal val rainParameters = RandomizeParticleGeneratorParameters(
    count = 400,
    particleWidthRange = IntRange(1, 2),
    particleHeightRange = IntRange(10, 20),
    minSpeed = DEFAULT_RAIN_MIN_SPEED,
    maxSpeed = DEFAULT_RAIN_MAX_SPEED,
    angleRange = IntRange(DEFAULT_RAIN_ANGLE_FROM, DEFAULT_RAIN_ANGLE_TO),
    minRotationalSpeed = 0f,
    maxRotationalSpeed = 0f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = { createRainParticle(IntRange(2, 6)) },
)
internal val pooParameters = RandomizeParticleGeneratorParameters(
    count = 30,
    particleWidthRange = IntRange(1, 10),
    particleHeightRange = IntRange(1, 10),
    minSpeed = 5f,
    maxSpeed = 15f,
    angleRange = IntRange(60, 120),
    minRotationalSpeed = DEFAULT_POO_MIN_ROTATIONAL_SPEED,
    maxRotationalSpeed = DEFAULT_POO_MAX_ROTATIONAL_SPEED,
    sourceEdges = setOf(SourceEdge.TOP, SourceEdge.LEFT, SourceEdge.RIGHT),
    shapeProvider = {
        ParticleShape.Text(
            text = "\uD83D\uDCA9", // poopoo
            fontSize = 14.sp,
            border = 1.dp,
            color = Color.Black,
        )
    },
)

internal val moneyParameters = RandomizeParticleGeneratorParameters(
    count = 30,
    particleWidthRange = IntRange(1, 10),
    particleHeightRange = IntRange(1, 10),
    minSpeed = 3f,
    maxSpeed = 10f,
    angleRange = IntRange(45, 135),
    minRotationalSpeed = 1f,
    maxRotationalSpeed = 3f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = {
        createMoneyParticle(fontSizeRange = IntRange(9, 24))
    },
)

internal fun createFlyingBirdParameters(resources: Resources) =
    RandomizeParticleGeneratorParameters(
        count = 5,
        randomizeInitialXY = false,
        particleWidthRange = IntRange(100, 200),
        particleHeightRange = IntRange(80, 100),
        minSpeed = 5f,
        maxSpeed = 30f,
        angleRange = IntRange(175, 185),
        minRotationalSpeed = 0f,
        maxRotationalSpeed = 0f,
        sourceEdges = setOf(SourceEdge.RIGHT),
        shapeProvider = {
            createBirdParticle(resources, R.drawable.flying_bird)
        },
    )

internal val twinkleStarParameters = RandomizeParticleGeneratorParameters(
    count = 250,
    particleWidthRange = IntRange(1, 4),
    particleHeightRange = IntRange(1, 4),
    sourceEdges = setOf(SourceEdge.TOP),
    startOffsetRange = IntRange(0, 60),
    shapeProvider = { createStarParticle(Color(0xFFFAFFFF), IntRange(1, 4)) },
)

internal val emotionParameters = RandomizeParticleGeneratorParameters(
    count = 16,
    randomizeInitialXY = false,
    particleWidthRange = IntRange(1, 10),
    particleHeightRange = IntRange(1, 10),
    minSpeed = 10f,
    maxSpeed = 15f,
    angleRange = IntRange(265, 275),
    sourceEdges = setOf(SourceEdge.BOTTOM),
    startOffsetRange = IntRange(0, 30),
    shapeProvider = {
        ParticleShape.Text(
            text = "\uD83D\uDE0D", // heart-eyes emoji
            fontSize = 20.sp,
            border = 1.dp,
            color = Color.White.copy(alpha = 1f),
        )
    },
)

internal val defaultSystemParameters = ParticleSystemParameters()
internal val sakuraSystemParameters = ParticleSystemParameters(fps = 120)
internal val emotionSystemParameters = ParticleSystemParameters(
    autoResetParticles = false,
    restartWhenAllDead = false,
)
