package com.github.wangyung.app.model

import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.RandomizeParticleGeneratorParameters

data class AnimationParameterSet(
    val generatorParameters: RandomizeParticleGeneratorParameters,
    val particleSystemParameters: ParticleSystemParameters
)
