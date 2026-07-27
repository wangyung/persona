package com.github.wangyung.persona.json.serializer

import com.github.wangyung.persona.particle.transformation.BlinkTransformationParameters
import com.github.wangyung.persona.particle.transformation.CompositeTransformationParameters
import com.github.wangyung.persona.particle.transformation.RotationTransformationParameters
import com.github.wangyung.persona.particle.transformation.ScaleAndFadeTransformationParameters
import com.github.wangyung.persona.particle.transformation.ScaleTransformationParameters
import com.github.wangyung.persona.particle.transformation.SequenceTransformationParameters
import com.github.wangyung.persona.particle.transformation.SineWaveTranslateTransformationParameters
import com.github.wangyung.persona.particle.transformation.TransformationParameters
import com.github.wangyung.persona.particle.transformation.TranslateTransformationParameters
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private const val DEFAULT_MIN_BLINK_FREQUENCY_FACTOR = 0.5f
private const val DEFAULT_MAX_BLINK_FREQUENCY_FACTOR = 2f

@Serializable
internal sealed class TransformationParametersSurrogate {
    @Serializable
    @SerialName("translate")
    data class Translate(val gravity: Float = 0f) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("rotation")
    object Rotation : TransformationParametersSurrogate()

    @Serializable
    @SerialName("blink")
    data class Blink(
        @Serializable(with = FloatRangeSerializer::class)
        val frequencyFactorRange: ClosedFloatingPointRange<Float> =
            DEFAULT_MIN_BLINK_FREQUENCY_FACTOR..DEFAULT_MAX_BLINK_FREQUENCY_FACTOR
    ) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("sineWaveTranslate")
    data class SineWaveTranslate(
        val frequencyFactor: Float = 2f,
        val amplitude: Float = 2f,
    ) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("scale")
    data class Scale(
        val xDelta: Float = 0f,
        val yDelta: Float = 0f,
    ) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("scaleAndFade")
    data class ScaleAndFade(
        val xDelta: Float = 0f,
        val yDelta: Float = 0f,
        val alphaDelta: Float = 0f,
    ) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("composite")
    data class Composite(
        val transformations: List<TransformationParametersSurrogate>
    ) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("sequence")
    data class Sequence(val steps: List<Step>) : TransformationParametersSurrogate() {
        @Serializable
        data class Step(val transformation: TransformationParametersSurrogate, val duration: Long)
    }
}

internal fun TransformationParameters.toSurrogate(): TransformationParametersSurrogate =
    when (this) {
        is TranslateTransformationParameters ->
            TransformationParametersSurrogate.Translate(gravity = gravity)
        is RotationTransformationParameters -> TransformationParametersSurrogate.Rotation
        is BlinkTransformationParameters ->
            TransformationParametersSurrogate.Blink(frequencyFactorRange = frequencyFactorRange)
        is SineWaveTranslateTransformationParameters ->
            TransformationParametersSurrogate.SineWaveTranslate(
                frequencyFactor = frequencyFactor,
                amplitude = amplitude,
            )
        is ScaleTransformationParameters ->
            TransformationParametersSurrogate.Scale(xDelta = xDelta, yDelta = yDelta)
        is ScaleAndFadeTransformationParameters ->
            TransformationParametersSurrogate.ScaleAndFade(
                xDelta = xDelta,
                yDelta = yDelta,
                alphaDelta = alphaDelta,
            )
        is CompositeTransformationParameters ->
            TransformationParametersSurrogate.Composite(
                transformations = parameters.map { it.toSurrogate() }
            )
        is SequenceTransformationParameters ->
            TransformationParametersSurrogate.Sequence(
                steps = steps.map {
                    TransformationParametersSurrogate.Sequence.Step(
                        transformation = it.parameters.toSurrogate(),
                        duration = it.duration,
                    )
                }
            )
    }

internal fun TransformationParametersSurrogate.toParameters(): TransformationParameters =
    when (this) {
        is TransformationParametersSurrogate.Translate ->
            TranslateTransformationParameters(gravity = gravity)
        is TransformationParametersSurrogate.Rotation -> RotationTransformationParameters()
        is TransformationParametersSurrogate.Blink ->
            BlinkTransformationParameters(frequencyFactorRange = frequencyFactorRange)
        is TransformationParametersSurrogate.SineWaveTranslate ->
            SineWaveTranslateTransformationParameters(
                frequencyFactor = frequencyFactor,
                amplitude = amplitude,
            )
        is TransformationParametersSurrogate.Scale ->
            ScaleTransformationParameters(xDelta = xDelta, yDelta = yDelta)
        is TransformationParametersSurrogate.ScaleAndFade ->
            ScaleAndFadeTransformationParameters(
                xDelta = xDelta,
                yDelta = yDelta,
                alphaDelta = alphaDelta,
            )
        is TransformationParametersSurrogate.Composite ->
            CompositeTransformationParameters(
                parameters = transformations.map { it.toParameters() }
            )
        is TransformationParametersSurrogate.Sequence ->
            SequenceTransformationParameters(
                steps = steps.map {
                    SequenceTransformationParameters.Step(
                        parameters = it.transformation.toParameters(),
                        duration = it.duration,
                    )
                }
            )
    }

/**
 * Serializes the [TransformationParameters] from the persona-core. The transformation type is
 * encoded in the `type` field: `translate`, `rotation`, `blink`, `sineWaveTranslate`, `scale`,
 * `scaleAndFade`, `composite` or `sequence`.
 */
object TransformationParametersSerializer : KSerializer<TransformationParameters> {
    override val descriptor: SerialDescriptor =
        TransformationParametersSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: TransformationParameters) =
        encoder.encodeSerializableValue(
            TransformationParametersSurrogate.serializer(),
            value.toSurrogate()
        )

    override fun deserialize(decoder: Decoder): TransformationParameters =
        decoder.decodeSerializableValue(TransformationParametersSurrogate.serializer())
            .toParameters()
}
