package com.github.wangyung.persona.json

import android.util.Size
import com.github.wangyung.persona.json.model.ParticleParameters
import com.github.wangyung.persona.json.serializer.ParticleGeneratorParametersSerializer
import com.github.wangyung.persona.json.serializer.ParticleSystemParametersSerializer
import com.github.wangyung.persona.json.serializer.TransformationParametersSerializer
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.particleSystem
import com.github.wangyung.persona.particle.transformation.TransformationParameters
import com.github.wangyung.persona.particle.transformation.toTransformation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

/**
 * The [Json] instance that knows how to serialize the parameters from the persona-core.
 */
val personaJson: Json = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(ParticleSystemParameters::class, ParticleSystemParametersSerializer)
        contextual(ParticleGeneratorParameters::class, ParticleGeneratorParametersSerializer)
        contextual(TransformationParameters::class, TransformationParametersSerializer)
    }
}

/**
 * Serializes the [ParticleParameters] to a json string.
 */
fun ParticleParameters.toJsonString(): String = personaJson.encodeToString(this)

/**
 * Deserializes the [ParticleParameters] from a json string.
 *
 * @throws kotlinx.serialization.SerializationException if the json is malformed or doesn't match
 * the [ParticleParameters] schema.
 */
fun particleParametersFromJson(jsonString: String): ParticleParameters =
    personaJson.decodeFromString(jsonString)

/**
 * Creates a [ParticleSystem] from the [ParticleParameters].
 *
 * @param dimension The dimension of the particle system.
 * @param shapeProvider Provides the shape of each particle. The shape is renderer specific and
 * can't be described in json, so it is provided by the caller. Use [ParticleParameters.shapeParameters]
 * to carry the custom shape settings.
 * @param autoStart If true, starts the particle system immediately.
 * @param coroutineDispatcher The dispatcher that runs the particle system.
 */
fun ParticleParameters.toParticleSystem(
    dimension: Size,
    shapeProvider: ShapeProvider,
    autoStart: Boolean = true,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
): ParticleSystem =
    particleSystem(
        dimension = dimension,
        parameters = systemParameters,
        generator = RandomizeParticleGenerator(
            parameters = generatorParameters,
            dimension = dimension,
            shapeProvider = shapeProvider,
        ),
        autoStart = autoStart,
        transformation = transformationParameters.toTransformation(),
        coroutineDispatcher = coroutineDispatcher,
    )

/**
 * Creates a [ParticleSystem] from a json string. See [particleParametersFromJson] and
 * [toParticleSystem].
 */
fun particleSystemFromJson(
    jsonString: String,
    dimension: Size,
    shapeProvider: ShapeProvider,
    autoStart: Boolean = true,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
): ParticleSystem =
    particleParametersFromJson(jsonString).toParticleSystem(
        dimension = dimension,
        shapeProvider = shapeProvider,
        autoStart = autoStart,
        coroutineDispatcher = coroutineDispatcher,
    )
