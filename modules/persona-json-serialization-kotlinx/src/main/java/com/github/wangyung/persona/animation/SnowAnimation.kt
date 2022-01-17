package com.github.wangyung.persona.animation

import com.github.wangyung.persona.json.model.ParticleParameters
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import com.github.wangyung.persona.particle.transformation.TranslateTransformationParameters

internal const val DEFAULT_RAIN_MIN_SPEED = 10f
internal const val DEFAULT_RAIN_MAX_SPEED = 30f
internal const val DEFAULT_RAIN_ANGLE_FROM = 85f
internal const val DEFAULT_RAIN_ANGLE_TO = 95f

internal val rainParameters = ParticleGeneratorParameters(
    count = 400,
    particleWidthRange = 1..2,
    particleHeightRange = 5..15,
    speedRange = DEFAULT_RAIN_MIN_SPEED..DEFAULT_RAIN_MAX_SPEED,
    angleRange = DEFAULT_RAIN_ANGLE_FROM..DEFAULT_RAIN_ANGLE_TO,
    zRotationalSpeedRange = 0f..0f,
    sourceEdges = setOf(SourceEdge.TOP),
)

internal val systemParameters = ParticleSystemParameters()

val testJson = ParticleParameters(
    name = "Snow",
    generatorParameters = rainParameters,
    systemParameters = systemParameters,
    transformationParameters = TranslateTransformationParameters()
)
