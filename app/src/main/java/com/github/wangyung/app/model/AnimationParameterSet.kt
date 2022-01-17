package com.github.wangyung.app.model

import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.transformation.TransformationParameters

data class AnimationParameterSet(
    val generatorParameters: ParticleGeneratorParameters,
    val particleSystemParameters: ParticleSystemParameters,
    val transformationParameters: TransformationParameters,
)
