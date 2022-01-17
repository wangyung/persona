package com.github.wangyung.app.viewmodel

import android.util.Size
import androidx.lifecycle.ViewModel
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.particleSystem
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import com.github.wangyung.persona.particle.transformation.TransformationParameters

class ParticlesViewModel : ViewModel() {
    var particleSystem: ParticleSystem? = null
        private set

    var generatorParameters: ParticleGeneratorParameters? = null
        private set
    var particleSystemParameters: ParticleSystemParameters? = null
        private set

    var transformationParameters: TransformationParameters? = null
        private set

    fun startNewParticlesSystem(
        systemParameters: ParticleSystemParameters,
        generatorParameters: ParticleGeneratorParameters,
        transformation: ParticleTransformation,
        dimension: Size,
        shapeProvider: ShapeProvider
    ) {
        particleSystem?.stop()
        this.generatorParameters = generatorParameters
        this.particleSystemParameters = systemParameters
        this.transformationParameters = transformationParameters
        particleSystem =
            particleSystem(
                dimension = dimension,
                parameters = systemParameters,
                generator = RandomizeParticleGenerator(
                    parameters = generatorParameters,
                    dimension = dimension,
                    shapeProvider = shapeProvider
                ),
                autoStart = true,
                transformation = transformation
            )
    }
}
