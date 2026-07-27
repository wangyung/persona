package com.github.wangyung.persona.particle.transformation

private const val DEFAULT_MIN_BLINK_FREQUENCY_FACTOR = 0.5f
private const val DEFAULT_MAX_BLINK_FREQUENCY_FACTOR = 2f

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
 * The parameters for [BlinkTransformation].
 */
class BlinkTransformationParameters(
    val frequencyFactorRange: ClosedFloatingPointRange<Float> =
        DEFAULT_MIN_BLINK_FREQUENCY_FACTOR..DEFAULT_MAX_BLINK_FREQUENCY_FACTOR
) : TransformationParameters

/**
 * The parameters for [SineWaveTranslateTransformation].
 */
class SineWaveTranslateTransformationParameters(
    val frequencyFactor: Float = 2f,
    val amplitude: Float = 2f,
) : TransformationParameters

/**
 * The parameters for [LinearScaleTransformation].
 */
class ScaleTransformationParameters(
    val xDelta: Float = 0f,
    val yDelta: Float = 0f,
) : TransformationParameters

/**
 * The parameters for [ScaleAndFadeTransformation].
 */
class ScaleAndFadeTransformationParameters(
    val xDelta: Float = 0f,
    val yDelta: Float = 0f,
    val alphaDelta: Float = 0f,
) : TransformationParameters

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
        is BlinkTransformationParameters ->
            BlinkTransformation(frequencyFactorRange = frequencyFactorRange)
        is SineWaveTranslateTransformationParameters ->
            SineWaveTranslateTransformation(
                frequencyFactor = frequencyFactor,
                amplitude = amplitude,
            )
        is ScaleTransformationParameters ->
            LinearScaleTransformation(xDelta = xDelta, yDelta = yDelta)
        is ScaleAndFadeTransformationParameters ->
            ScaleAndFadeTransformation(xDelta = xDelta, yDelta = yDelta, alphaDelta = alphaDelta)
        is CompositeTransformationParameters ->
            CompositeTransformation(parameters.map { it.toTransformation() })
        is SequenceTransformationParameters ->
            SequenceTransformation().apply {
                steps.forEach { step ->
                    add(step.parameters.toTransformation(), step.duration)
                }
            }
    }
