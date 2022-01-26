package com.github.wangyung.persona.json.model

import androidx.annotation.Keep
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.transformation.TransformationParameters
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The particle parameters for serialize/deserialize.
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
    val shapeParameters: Map<String, @Contextual Any>? = null
)

private val json = Json { ignoreUnknownKeys = true }
fun ParticleParameters.toJsonString(): String = json.encodeToString(this)
