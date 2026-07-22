package com.github.wangyung.persona.json.serializer

import com.github.wangyung.persona.particle.transformation.CompositeTransformationParameters
import com.github.wangyung.persona.particle.transformation.RotationTransformationParameters
import com.github.wangyung.persona.particle.transformation.SequenceTransformationParameters
import com.github.wangyung.persona.particle.transformation.TransformationParameters
import com.github.wangyung.persona.particle.transformation.TranslateTransformationParameters
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal sealed class TransformationParametersSurrogate {
    @Serializable
    @SerialName("translate")
    data class Translate(val gravity: Float = 0f) : TransformationParametersSurrogate()

    @Serializable
    @SerialName("rotation")
    object Rotation : TransformationParametersSurrogate()

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
 * encoded in the `type` field: `translate`, `rotation`, `composite` or `sequence`.
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
