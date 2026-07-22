package com.github.wangyung.persona.particle.transformation

/**
 * The parameters for transformation
 */
sealed interface TransformationParameters

/**
 * The parameters for [LinearTranslateTransformation].
 */
class TranslateTransformationParameters(val gravity: Float = 0f) : TransformationParameters

/**
 * The parameters for [LinearRotationTransformation].
 */
class RotationTransformationParameters : TransformationParameters

/**
 * The parameters for [CompositeTransformation]. All [parameters] are applied at the same time.
 */
class CompositeTransformationParameters(
    val parameters: List<TransformationParameters>
) : TransformationParameters

/**
 * The parameters for [SequenceTransformation]. Each [Step] runs sequentially for its duration.
 */
class SequenceTransformationParameters(
    val steps: List<Step>
) : TransformationParameters {
    /**
     * A single step in the sequence. The [parameters] describe the transformation that runs for
     * the given [duration] (in iterations).
     */
    class Step(val parameters: TransformationParameters, val duration: Long)
}

/**
 * Creates the [ParticleTransformation] that is described by the [TransformationParameters].
 */
fun TransformationParameters.toTransformation(): ParticleTransformation =
    when (this) {
        is TranslateTransformationParameters -> LinearTranslateTransformation(gravity = gravity)
        is RotationTransformationParameters -> LinearRotationTransformation()
        is CompositeTransformationParameters ->
            CompositeTransformation(parameters.map { it.toTransformation() })
        is SequenceTransformationParameters ->
            SequenceTransformation().apply {
                steps.forEach { step ->
                    add(step.parameters.toTransformation(), step.duration)
                }
            }
    }
