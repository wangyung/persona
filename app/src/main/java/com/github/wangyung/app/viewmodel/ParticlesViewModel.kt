package com.github.wangyung.app.viewmodel

import android.util.Size
import androidx.lifecycle.ViewModel
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.parameter.RandomizeParticleGeneratorParameters
import com.github.wangyung.persona.particle.transformation.ParticleTransformation

class ParticlesViewModel : ViewModel() {
    var particleSystem: ParticleSystem? = null
        private set

    var generatorParameters: RandomizeParticleGeneratorParameters? = null
        private set
    var particleSystemParameters: ParticleSystemParameters? = null
        private set

    fun startNewParticlesSystem(
        systemParameters: ParticleSystemParameters,
        generatorParameters: RandomizeParticleGeneratorParameters,
        transformation: ParticleTransformation,
        size: Size,
    ) {
        particleSystem?.stop()
        this.generatorParameters = generatorParameters
        this.particleSystemParameters = systemParameters
        particleSystem =
            ParticleSystem(
                size = size,
                parameters = systemParameters,
                generator = RandomizeParticleGenerator(
                    parameters = generatorParameters,
                    size = size,
                ),
                autoStart = true,
                transformation = transformation
            )
    }
}
