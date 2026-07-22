package com.github.wangyung.persona.json.model

import androidx.annotation.Keep
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.transformation.TransformationParameters
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The particle parameters for serialize/deserialize. It describes an animation without the shape.
 * The shape is renderer specific, so the caller provides the `ShapeProvider` when creating the
 * particle system. The [shapeParameters] can carry arbitrary json for the caller to create the
 * shapes.
 */
@Keep
@Serializable
data class ParticleParameters(
    val name: String,
    @Contextual
    val systemParameters: ParticleSystemParameters,
    @Contextual
    val generatorParameters: ParticleGeneratorParameters,
    @Contextual
    val transformationParameters: TransformationParameters,
    val shapeParameters: JsonObject? = null,
)
