package com.github.wangyung.app.model

import android.content.Context
import android.util.Size
import com.github.wangyung.persona.json.model.ParticleParameters
import com.github.wangyung.persona.json.particleParametersFromJson
import com.github.wangyung.persona.json.toParticleSystem
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.generator.ShapeProvider

/**
 * Loads the [ParticleParameters] of the animation from the json file
 * ([AnimationType.jsonAssetPath]) in the assets.
 */
fun AnimationType.toParticleParameters(context: Context): ParticleParameters =
    context.assets.open(jsonAssetPath).bufferedReader().use { it.readText() }
        .let(::particleParametersFromJson)

/**
 * Converts the [ParticleParameters] to the [AnimationParameterSet] used by the demo screens.
 */
fun ParticleParameters.toAnimationParameterSet(): AnimationParameterSet =
    AnimationParameterSet(
        generatorParameters = generatorParameters,
        particleSystemParameters = systemParameters,
        transformationParameters = transformationParameters,
    )

/**
 * Creates the [ShapeProvider] from the [ParticleParameters.shapeParameters]. See
 * [ShapeParameters] for the schema.
 */
fun ParticleParameters.toShapeProvider(context: Context): ShapeProvider =
    requireNotNull(shapeParameters) { "shapeParameters is missing in the animation json: $name" }
        .toShapeParameters()
        .toShapeProvider(context)

/**
 * Creates a [ParticleSystem] for this animation type entirely from its json asset file.
 */
fun AnimationType.particleSystemFromJsonAsset(
    context: Context,
    dimension: Size,
    autoStart: Boolean = true,
): ParticleSystem = toParticleParameters(context).let { parameters ->
    parameters.toParticleSystem(
        dimension = dimension,
        shapeProvider = parameters.toShapeProvider(context),
        autoStart = autoStart,
    )
}
